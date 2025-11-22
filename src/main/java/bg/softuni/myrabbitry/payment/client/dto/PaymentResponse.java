package bg.softuni.myrabbitry.payment.client.dto;

import bg.softuni.myrabbitry.subscription.model.CardTier;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {

    private String status;

    private BigDecimal amount;

    private CardTier cardTier;

    private String failureReason;

    private int lastFourDigitsOfCardNumber;

    private UUID transactionId;

    private SubscriptionPeriod period;

    private SubscriptionType subscriptionType;
}
