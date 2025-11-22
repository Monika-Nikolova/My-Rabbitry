package bg.softuni.myrabbitry.payment.client.dto;

import bg.softuni.myrabbitry.subscription.model.CardTier;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Data
@Builder
public class Payment {

    private UUID userId;

    private BigDecimal amount;

    private String sixteenDigitCode;

    private YearMonth dateOfExpiry;

    private String cvvCode;

    private CardTier cardTier;

    private String subscriptionType;

    private String period;
}
