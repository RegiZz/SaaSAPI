package pl.regizz.saasapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.regizz.saasapi.domain.model.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}