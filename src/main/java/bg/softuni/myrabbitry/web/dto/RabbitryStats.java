package bg.softuni.myrabbitry.web.dto;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RabbitryStats {

    private int rabbitsInRabbitry;

    private int totalRabbits;

    private int does;

    private int bucks;

    private int bornInRabbitry;

    private int mostRabbitsInYear;

    private Map<Integer, Integer> countRabbitsByYear;
}
