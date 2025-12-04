package bg.softuni.myrabbitry.rabbit.model;

import lombok.Getter;

@Getter
public enum Sex {

    FEMALE("Female"),
    MALE("Male");

    private final String displayName;

    Sex(String displayName) {
        this.displayName = displayName;
    }
}
