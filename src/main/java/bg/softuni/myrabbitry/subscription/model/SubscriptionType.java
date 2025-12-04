package bg.softuni.myrabbitry.subscription.model;

import lombok.Getter;

@Getter
public enum SubscriptionType {

    FAMILY_HOBBY_FARM("family hobby farm"),
    LARGE_FARM("large farm"),
    INDUSTRIAL_FARM("industrial farm");

    private final String displayName;

    SubscriptionType(String displayName) {
        this.displayName = displayName;
    }
}
