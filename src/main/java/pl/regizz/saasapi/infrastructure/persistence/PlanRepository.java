package pl.regizz.saasapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.regizz.saasapi.domain.model.Plan;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByCode(String code);
}
