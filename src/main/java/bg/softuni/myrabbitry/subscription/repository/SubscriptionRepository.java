package bg.softuni.myrabbitry.subscription.repository;

import bg.softuni.myrabbitry.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findAllByExpirationOnAfter(LocalDateTime now);
}
