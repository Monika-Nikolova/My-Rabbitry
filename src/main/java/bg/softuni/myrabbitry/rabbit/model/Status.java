package bg.softuni.myrabbitry.rabbit.model;

import lombok.Getter;

@Getter
public enum Status {

    FOR_BREEDING("For Breeding"),
    FOR_MEAT("For Meat"),
    PROCESSED("Processed"),
    SOLD("Sold"),
    INACTIVE("Inactive");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }
}
