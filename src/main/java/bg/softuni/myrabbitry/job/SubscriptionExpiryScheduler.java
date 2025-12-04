package bg.softuni.myrabbitry.job;

import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionStatus;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionExpiryScheduler {

    private final SubscriptionService subscriptionService;

    public SubscriptionExpiryScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(fixedDelay = 1800000)
    public void checkSubscriptionExpired() {

        List<Subscription> subscriptions = subscriptionService.getAllExpired();

        subscriptions.forEach(subscription -> {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionService.upsert(subscription);
            subscriptionService.createNewSubscription(subscription.getOwner(), SubscriptionPeriod.YEARLY, SubscriptionType.FAMILY_HOBBY_FARM);
        });
    }
}
