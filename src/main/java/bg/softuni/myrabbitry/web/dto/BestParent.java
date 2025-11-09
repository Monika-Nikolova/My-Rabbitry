package bg.softuni.myrabbitry.web.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BestParent {
    private String code;
    private String name;
    private UUID id;
    private LocalDate birthDate;
    private int countLiters;
    private int bornKids;
    private int weanedKids;
    private double deathPercentage;
    private double lactatingQuantity;
}
