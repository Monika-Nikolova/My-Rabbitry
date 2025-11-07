package bg.softuni.myrabbitry.rabbit.model;

import lombok.Getter;

@Getter
public enum EyeColour {
    BROWN("Brown"),
    BLUE("Blue"),
    RED("Red"),
    MARBLED("Marbled"),//brown and blue in one eye
    COMBINATION("Combination");//one brown one blue eye

    private final String displayName;

    EyeColour(String displayName) {
        this.displayName = displayName;
    }
}
