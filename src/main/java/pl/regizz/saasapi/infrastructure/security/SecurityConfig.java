package pl.regizz.saasapi.infrastructure.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/plans/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/subscriptions/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/limits/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(Customizer.withDefaults())
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(SecurityProperties properties, PasswordEncoder passwordEncoder) {
        List<SecurityProperties.UserConfig> configuredUsers = properties.getUsers();
        if (configuredUsers.isEmpty()) {
            throw new IllegalStateException("No users configured. Set app.security.users in application properties.");
        }

        List<UserDetails> users = configuredUsers.stream()
                .map(user -> User.builder()
                        .username(requireText(user.getUsername(), "username"))
                        .password(encodeIfNeeded(requireText(user.getPassword(), "password"), passwordEncoder))
                        .roles(requireRoles(user.getRoles()).toArray(String[]::new))
                        .build())
                .toList();

        return new InMemoryUserDetailsManager(users);
    }

    private static String encodeIfNeeded(String password, PasswordEncoder passwordEncoder) {
        if (password.startsWith("{")) {
            return password;
        }
        return passwordEncoder.encode(password);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("User " + fieldName + " must not be blank.");
        }
        return value;
    }

    private static List<String> requireRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalStateException("User roles must not be empty.");
        }
        return roles;
    }
}
