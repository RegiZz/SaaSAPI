package pl.regizz.saasapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.regizz.saasapi.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}