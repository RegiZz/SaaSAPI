package pl.regizz.saasapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.regizz.saasapi.domain.model.User;
import pl.regizz.saasapi.infrastructure.persistence.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPersistUserAndGenerateId() {
        User user = new User("test@mail.pl");

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@mail.pl");
    }
}
