package bg.softuni.myrabbitry.rabbit.model;

import lombok.Getter;

@Getter
public enum CoatLength {

    SHORT("Short"),
    MEDIUM("Medium"),
    LONG("Long");

    private final String displayName;

    CoatLength(String displayName) {
        this.displayName = displayName;
    }
}
