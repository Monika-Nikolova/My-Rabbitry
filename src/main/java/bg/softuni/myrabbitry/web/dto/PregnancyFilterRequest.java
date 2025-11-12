package bg.softuni.myrabbitry.web.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PregnancyFilterRequest {
    private String mother;

    private String father;

    private String sortCriteria;
}
