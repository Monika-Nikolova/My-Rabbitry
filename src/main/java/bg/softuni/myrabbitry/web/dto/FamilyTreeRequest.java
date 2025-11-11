package bg.softuni.myrabbitry.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FamilyTreeRequest {

    @NotBlank
    private String code;
}
