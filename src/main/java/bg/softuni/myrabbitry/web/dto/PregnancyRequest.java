package bg.softuni.myrabbitry.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PregnancyRequest {
    @NotBlank
    private String mother;

    private String father;

    private LocalDate dayOfFertilization;

    private LocalDate dateOfBirth;

    private Integer countBornKids;

    private Integer countWeanedKids;

    private Double totalWeightKidsDay1;

    private Double totalWeightKidsDay20;

    @NotNull
    private boolean isFalsePregnancy;

    @NotNull
    private boolean isCannibalismPresent;

    @NotNull
    private boolean hasAbort;

    @NotNull
    private boolean wasPregnant;
}
