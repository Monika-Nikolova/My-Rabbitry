package bg.softuni.myrabbitry.subscription.model;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("Debit Card"),
    DEBIT_CARD("Credit Card");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
}
