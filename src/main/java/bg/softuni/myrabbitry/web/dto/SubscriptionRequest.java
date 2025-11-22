package bg.softuni.myrabbitry.web.dto;

import bg.softuni.myrabbitry.subscription.model.CardTier;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequest {

    @NotNull
    private SubscriptionPeriod period;

    @NotNull
    private CardTier cardTier;
}
