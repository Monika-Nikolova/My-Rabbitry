package bg.softuni.myrabbitry.subscription.service;

import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionStatus;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.repository.SubscriptionRepository;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.web.dto.SubscriptionRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

    @Transactional
    public void createNewSubscription(User user, SubscriptionRequest subscriptionRequest, SubscriptionType subscriptionType) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationOn;
        if (subscriptionRequest.getPeriod() == SubscriptionPeriod.MONTHLY) {
            expirationOn = now.plusMonths(1);
        } else {
            expirationOn = now.plusYears(1);
        }

        Subscription newSubscription = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .period(subscriptionRequest.getPeriod())
                .type(subscriptionType)
                .price(getSubscriptionPrice(subscriptionType, subscriptionRequest.getPeriod()))
                .createdOn(LocalDateTime.now())
                .expirationOn(expirationOn)
                .owner(user)
                .build();

        if (subscriptionType == SubscriptionType.INDUSTRIAL_FARM || subscriptionType == SubscriptionType.LARGE_FARM) {
            newSubscription.setPaymentMethod(subscriptionRequest.getPaymentMethod());
        }

        Subscription currentSubscription = user.getSubscriptions().get(0);
        currentSubscription.setStatus(SubscriptionStatus.EXPIRED);
        currentSubscription.setExpirationOn(now);

        subscriptionRepository.save(currentSubscription);
        subscriptionRepository.save(newSubscription);
    }

    private BigDecimal getSubscriptionPrice(SubscriptionType type, SubscriptionPeriod period) {

        if (type == SubscriptionType.FAMILY_HOBBY_FARM) {
            return BigDecimal.ZERO;
        } else if (type == SubscriptionType.LARGE_FARM && period == SubscriptionPeriod.MONTHLY) {
            return new BigDecimal("5");
        } else if (type == SubscriptionType.LARGE_FARM && period == SubscriptionPeriod.YEARLY) {
            return new BigDecimal("55");
        } else if (type == SubscriptionType.INDUSTRIAL_FARM && period == SubscriptionPeriod.MONTHLY) {
            return new BigDecimal("40");
        } else if (type == SubscriptionType.INDUSTRIAL_FARM && period == SubscriptionPeriod.YEARLY) {
            return new BigDecimal("450");
        }

        throw new RuntimeException(String.format("Price not found for type [%s] and period [%s]", type, period));
    }
}
