package bg.softuni.myrabbitry.subscription.service;

import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionStatus;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.repository.SubscriptionRepository;
import bg.softuni.myrabbitry.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription creatDefaultSubscription(User user) {
        Subscription subscription = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .period(SubscriptionPeriod.YEARLY)
                .type(SubscriptionType.FAMILY_HOBBY_FARM)
                .price(BigDecimal.ZERO)
                .createdOn(LocalDateTime.now())
                .expirationOn(LocalDateTime.now().plusYears(1))
                .owner(user)
                .build();

        return subscriptionRepository.save(subscription);
    }
}
