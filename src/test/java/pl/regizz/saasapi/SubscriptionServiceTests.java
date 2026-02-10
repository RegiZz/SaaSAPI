package pl.regizz.saasapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.regizz.saasapi.domain.event.SubscriptionEvent;
import pl.regizz.saasapi.domain.model.*;
import pl.regizz.saasapi.domain.service.SubscriptionService;
import pl.regizz.saasapi.infrastructure.persistence.PlanRepository;
import pl.regizz.saasapi.infrastructure.persistence.SubscriptionEventRepository;
import pl.regizz.saasapi.infrastructure.persistence.SubscriptionRepository;
import pl.regizz.saasapi.infrastructure.persistence.UserRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SubscriptionServiceTests {

    @MockBean
    private SubscriptionRepository subscriptionRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PlanRepository planRepository;
    @MockBean
    private SubscriptionEventRepository eventRepository;
    @Autowired
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository, userRepository, planRepository, eventRepository);
    }

    @Test
    void startTrial_savesSubscriptionAndEvent() {
        User user = new User("user@example.com");
        Plan plan = new Plan("BASIC", BigDecimal.valueOf(29), BillingPeriod.MONTHLY, java.util.Map.of("maxUsers", 10, "maxProjects", 20));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(2L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findByUserAndStatusIn(eq(user), anyList())).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subscription result = subscriptionService.startTrial(1L, 2L, Instant.now().plusSeconds(86400), false);

        assertNotNull(result);
        assertEquals(SubscriptionStatus.TRIAL, result.getStatus());
        assertFalse(result.isAutoRenew());
        verify(subscriptionRepository).save(any(Subscription.class));
        verify(eventRepository).save(any(SubscriptionEvent.class));
    }

}
