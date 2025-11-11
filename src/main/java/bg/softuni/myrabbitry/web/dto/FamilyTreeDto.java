package bg.softuni.myrabbitry.web.dto;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FamilyTreeDto {
    private Rabbit child;
    private FamilyTreeDto mother;
    private FamilyTreeDto father;
}
