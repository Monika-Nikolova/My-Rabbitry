package bg.softuni.myrabbitry.rabbit.model;

import lombok.Getter;

@Getter
public enum EyeColour {

    BROWN("Brown"),
    BLUE("Blue"),
    RED("Red"),
    MARBLED("Marbled"),
    COMBINATION("Combination");

    private final String displayName;

    EyeColour(String displayName) {
        this.displayName = displayName;
    }
}
