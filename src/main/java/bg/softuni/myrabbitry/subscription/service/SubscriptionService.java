package bg.softuni.myrabbitry.subscription.service;

import bg.softuni.myrabbitry.event.ChangedSubscriptionEvent;
import bg.softuni.myrabbitry.exception.DeleteSubscriptionNotAllowedException;
import bg.softuni.myrabbitry.exception.SubscriptionNotFoundException;
import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionStatus;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.repository.SubscriptionRepository;
import bg.softuni.myrabbitry.user.model.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
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
                .isDeleted(false)
                .owner(user)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        log.info("Default subscription with id [{}] has been created for user [{}]", savedSubscription.getId(), user.getId());

        return savedSubscription;
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
                .isDeleted(false)
                .owner(user)
                .build();

        List<String> permissions = getPermissions(subscriptionType);

        applicationEventPublisher.publishEvent(new ChangedSubscriptionEvent(permissions, user));

        Subscription currentSubscription = user.getSubscriptions().get(0);
        currentSubscription.setStatus(SubscriptionStatus.EXPIRED);
        currentSubscription.setExpirationOn(now);

        user.setSubscriptions(List.of(newSubscription, currentSubscription));

        subscriptionRepository.save(currentSubscription);
        subscriptionRepository.save(newSubscription);

        log.info("User [{}] changed their subscription to {} {}", user.getId(), period, subscriptionType);
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

        throw new IllegalArgumentException(String.format("Price not found for type [%s] and period [%s]", type, period));
    }

    public List<Subscription> getAllExpired() {
        return subscriptionRepository.findAllByExpirationOnBeforeAndStatus(LocalDateTime.now(), SubscriptionStatus.ACTIVE);
    }

    public void upsert(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public void deleteSubscription(UUID id) {

        Subscription subscription = getById(id);

        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            throw new DeleteSubscriptionNotAllowedException("Cannot delete subscription with active status");
        }

        subscription.setDeleted(true);

        subscriptionRepository.save(subscription);

        log.info("Subscription with id [{}] has been deleted", id);
    }

    private Subscription getById(UUID id) {
        return subscriptionRepository.findById(id).orElseThrow(() -> new SubscriptionNotFoundException(String.format("Subscription with id %s not found", id)));
    }
}
