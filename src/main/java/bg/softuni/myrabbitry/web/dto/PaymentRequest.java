package bg.softuni.myrabbitry.web.dto;

import bg.softuni.myrabbitry.subscription.model.CardTier;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.YearMonth;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @NotNull
    private SubscriptionType subscriptionType;

    @NotBlank
    @Size(min = 15, max = 16)
    @Pattern(regexp = "\\d+", message = "Only digits allowed")
    private String sixteenDigitCode;

    @NotNull
    private YearMonth expiry;

    @NotBlank
    @Size(min = 3, max = 4)
    @Pattern(regexp = "\\d+", message = "Only digits allowed")
    private String cvv;

    @NotNull
    private CardTier cardTier;

    @NotNull
    private SubscriptionPeriod period;
}
