package bg.softuni.myrabbitry.web.dto;

import bg.softuni.myrabbitry.rabbit.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RabbitRequest {
    @URL
    private String photoUrl;

    private String name;

    @NotBlank
    private String code;

    @Size(max = 150)
    private String description;

    private String motherCode;

    private String fatherCode;

    private LocalDate birthDate;

    @NotNull
    private Sex sex;

    @NotBlank
    private String colour;

    @NotBlank
    private String pattern;

    @NotNull
    private EyeColour eyeColour;

    @NotNull
    private EarShape earShape;

    @NotNull
    private CoatLength coatLength;

    private String breed;

    private LocalDate vaccinatedOn;

    @NotNull
    private Status status;
}
