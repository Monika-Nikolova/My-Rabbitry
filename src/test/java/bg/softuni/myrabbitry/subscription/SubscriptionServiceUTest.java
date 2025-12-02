package bg.softuni.myrabbitry.subscription;

import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionStatus;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.repository.SubscriptionRepository;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceUTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void whenCreateNewSubscription_andItIsFamilyHobbyFarm_thenPrice0_andPersistNewSubscription_andUpdateOldSubscription() {

        Subscription subscription = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .build();
        User user = User.builder()
                .subscriptions(List.of(subscription))
                .build();
        SubscriptionPeriod subscriptionPeriod = SubscriptionPeriod.YEARLY;
        SubscriptionType subscriptionType = SubscriptionType.FAMILY_HOBBY_FARM;

        subscriptionService.createNewSubscription(user, subscriptionPeriod, subscriptionType);

        assertEquals(SubscriptionStatus.EXPIRED, subscription.getStatus());
        assertEquals(2, user.getSubscriptions().size());
        assertEquals(BigDecimal.ZERO, user.getSubscriptions().get(0).getPrice());
        verify(subscriptionRepository).save(subscription);
        verify(subscriptionRepository).save(user.getSubscriptions().get(0));
    }

    @Test
    void whenCreateNewSubscription_andItIsLargeFarmMonthly_thenPrice5_andPersistNewSubscription_andUpdateOldSubscription() {

        Subscription subscription = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .build();
        User user = User.builder()
                .subscriptions(List.of(subscription))
                .build();
        SubscriptionPeriod subscriptionPeriod = SubscriptionPeriod.MONTHLY;
        SubscriptionType subscriptionType = SubscriptionType.LARGE_FARM;

        subscriptionService.createNewSubscription(user, subscriptionPeriod, subscriptionType);

        assertEquals(SubscriptionStatus.EXPIRED, subscription.getStatus());
        assertEquals(2, user.getSubscriptions().size());
        assertEquals(BigDecimal.valueOf(5), user.getSubscriptions().get(0).getPrice());
        verify(subscriptionRepository).save(subscription);
        verify(subscriptionRepository).save(user.getSubscriptions().get(0));
    }

    @Test
    void whenCreateNewSubscription_andItIsIndustrialFarmYearly_thenPrice450_andPersistNewSubscription_andUpdateOldSubscription() {

        Subscription subscription = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .build();
        User user = User.builder()
                .subscriptions(List.of(subscription))
                .build();
        SubscriptionPeriod subscriptionPeriod = SubscriptionPeriod.YEARLY;
        SubscriptionType subscriptionType = SubscriptionType.INDUSTRIAL_FARM;

        subscriptionService.createNewSubscription(user, subscriptionPeriod, subscriptionType);

        assertEquals(SubscriptionStatus.EXPIRED, subscription.getStatus());
        assertEquals(2, user.getSubscriptions().size());
        assertEquals(BigDecimal.valueOf(450), user.getSubscriptions().get(0).getPrice());
        verify(subscriptionRepository).save(subscription);
        verify(subscriptionRepository).save(user.getSubscriptions().get(0));
    }
}
