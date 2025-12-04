package bg.softuni.myrabbitry.rabbit.model;

import lombok.Getter;

@Getter
public enum EarShape {

    UPRIGHT("Upright"),
    LOP("Lop"),
    HALF_LOP("Half lop");

    private final String displayName;

    EarShape(String displayName) {
        this.displayName = displayName;
    }
}
