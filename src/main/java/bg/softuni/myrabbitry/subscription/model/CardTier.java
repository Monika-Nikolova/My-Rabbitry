package bg.softuni.myrabbitry.subscription.model;

import lombok.Getter;

@Getter
public enum CardTier {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMERICAN_EXPRESS("American Express");

    private final String displayName;

    CardTier(String displayName) {
        this.displayName = displayName;
    }
}
