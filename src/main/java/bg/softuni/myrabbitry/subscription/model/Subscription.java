package bg.softuni.myrabbitry.subscription.model;

import bg.softuni.myrabbitry.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionPeriod period;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionType type;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime expirationOn;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;
}
