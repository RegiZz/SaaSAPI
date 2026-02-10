package pl.regizz.saasapi.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Configuration
public class DynamicLimitsConfig {

    public record ConfigData(List<String> limits) {}

    @Bean
    public List<String> availableLimits() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Path path = Paths.get("config.json");
            if (Files.exists(path)) {
                ConfigData data = mapper.readValue(path.toFile(), ConfigData.class);
                return data.limits();
            }
            
            ClassPathResource resource = new ClassPathResource("config.json");
            if (resource.exists()) {
                ConfigData data = mapper.readValue(resource.getInputStream(), ConfigData.class);
                return data.limits();
            }
        } catch (IOException e) {
            // Log error in real app
            System.err.println("Could not load config.json: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}
