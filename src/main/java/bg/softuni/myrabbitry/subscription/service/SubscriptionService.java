package bg.softuni.myrabbitry.subscription.service;

import bg.softuni.myrabbitry.Event.ChangedSubscriptionEvent;
import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionStatus;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.repository.SubscriptionRepository;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.web.dto.SubscriptionRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionService {

    private final String PREGNANCY_DETAILS_PERMISSION = "view_pregnancy_details";
    private final String CREATE_PREGNANCY_DETAILS_PERMISSION = "create_pregnancy_details";
    private final String EDIT_PREGNANCY_DETAILS_PERMISSION = "edit_pregnancy_details";
    private final String MY_RABBITS_PERMISSION = "view_my_rabbits";
    private final String CREATE_RABBIT_PERMISSION = "create_rabbits";
    private final String EDIT_RABBIT_PERMISSION = "edit_rabbits";
    private final String FAMILY_TREE_PERMISSION = "view_family_tree";
    private final String OVERVIEW_PERMISSION = "view_overview";
    private final String MATERNITY_WARD_PERMISSION = "view_maternity_ward";

    private final SubscriptionRepository subscriptionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.subscriptionRepository = subscriptionRepository;
        this.applicationEventPublisher = applicationEventPublisher;
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
    public void createNewSubscription(User user, SubscriptionPeriod period, SubscriptionType subscriptionType) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationOn;
        if (period == SubscriptionPeriod.MONTHLY) {
            expirationOn = now.plusMonths(1);
        } else {
            expirationOn = now.plusYears(1);
        }

        Subscription newSubscription = Subscription.builder()
                .status(SubscriptionStatus.ACTIVE)
                .period(period)
                .type(subscriptionType)
                .price(getSubscriptionPrice(subscriptionType, period))
                .createdOn(LocalDateTime.now())
                .expirationOn(expirationOn)
                .owner(user)
                .build();

        List<String> permissions = getPermissions(subscriptionType);

        applicationEventPublisher.publishEvent(new ChangedSubscriptionEvent(permissions, user));

        Subscription currentSubscription = user.getSubscriptions().get(0);
        currentSubscription.setStatus(SubscriptionStatus.EXPIRED);
        currentSubscription.setExpirationOn(now);

        subscriptionRepository.save(currentSubscription);
        subscriptionRepository.save(newSubscription);
    }

    private List<String> getPermissions(SubscriptionType subscriptionType) {
        List<String> permissions;
        if (subscriptionType == SubscriptionType.FAMILY_HOBBY_FARM) {
            permissions = List.of(PREGNANCY_DETAILS_PERMISSION, MY_RABBITS_PERMISSION, CREATE_PREGNANCY_DETAILS_PERMISSION, EDIT_PREGNANCY_DETAILS_PERMISSION, CREATE_RABBIT_PERMISSION, EDIT_RABBIT_PERMISSION);
        } else if (subscriptionType == SubscriptionType.LARGE_FARM) {
            permissions = List.of(PREGNANCY_DETAILS_PERMISSION, MY_RABBITS_PERMISSION, CREATE_PREGNANCY_DETAILS_PERMISSION, EDIT_PREGNANCY_DETAILS_PERMISSION, CREATE_RABBIT_PERMISSION, EDIT_RABBIT_PERMISSION, FAMILY_TREE_PERMISSION);
        } else {
            permissions = List.of(PREGNANCY_DETAILS_PERMISSION, MY_RABBITS_PERMISSION, CREATE_PREGNANCY_DETAILS_PERMISSION, EDIT_PREGNANCY_DETAILS_PERMISSION, CREATE_RABBIT_PERMISSION, EDIT_RABBIT_PERMISSION, FAMILY_TREE_PERMISSION, OVERVIEW_PERMISSION,  MATERNITY_WARD_PERMISSION);
        }
        return permissions;
    }

    public BigDecimal getSubscriptionPrice(SubscriptionType type, SubscriptionPeriod period) {

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

    public List<Subscription> getAllExpired() {
        return subscriptionRepository.findAllByExpirationOnBefore(LocalDateTime.now());
    }

    public void upsert(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }
}
