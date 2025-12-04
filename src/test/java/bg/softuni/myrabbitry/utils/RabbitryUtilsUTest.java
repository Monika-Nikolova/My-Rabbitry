package bg.softuni.myrabbitry.utils;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.rabbit.model.Sex;
import bg.softuni.myrabbitry.rabbit.model.Status;
import bg.softuni.myrabbitry.web.dto.RabbitryStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RabbitryUtilsUTest {

    @Test
    void whenGetRabbitryStats_thenReturnRabbitryStats() {

        Rabbit rabbit = Rabbit.builder()
                .status(Status.FOR_BREEDING)
                .sex(Sex.FEMALE)
                .build();
        Rabbit rabbit2 = Rabbit.builder()
                .status(Status.FOR_BREEDING)
                .sex(Sex.MALE)
                .build();
        Rabbit rabbit3 = Rabbit.builder()
                .status(Status.FOR_MEAT)
                .sex(Sex.MALE)
                .mother(rabbit)
                .birthDate(LocalDate.of(2024, 5, 23))
                .build();
        Rabbit rabbit4 = Rabbit.builder()
                .status(Status.FOR_MEAT)
                .sex(Sex.FEMALE)
                .father(rabbit2)
                .birthDate(LocalDate.of(2025, 5, 23))
                .build();
        Rabbit rabbit5 = Rabbit.builder()
                .status(Status.FOR_MEAT)
                .sex(Sex.MALE)
                .father(rabbit2)
                .birthDate(LocalDate.of(2025, 9, 29))
                .build();
        Rabbit rabbit6 = Rabbit.builder()
                .status(Status.PROCESSED)
                .build();

        RabbitryStats rabbitryStats = RabbitryUtils.getRabbitryStats(List.of(rabbit, rabbit2, rabbit3, rabbit4, rabbit5, rabbit6));

        assertEquals(5, rabbitryStats.getRabbitsInRabbitry());
        assertEquals(6, rabbitryStats.getTotalRabbits());
        assertEquals(1, rabbitryStats.getBucks());
        assertEquals(1, rabbitryStats.getDoes());
        assertEquals(3, rabbitryStats.getBornInRabbitry());
        assertEquals(2, rabbitryStats.getMostRabbitsInYear());
        assertEquals(2, rabbitryStats.getCountRabbitsByYear().get(2025));
        assertEquals(1, rabbitryStats.getCountRabbitsByYear().get(2024));
    }

    @Test
    void whenGetStatusPercentage_and3RabbitsFrom6AreForMeat_thenReturn50Percent() {

        Rabbit rabbit = Rabbit.builder()
                .status(Status.FOR_BREEDING)
                .sex(Sex.FEMALE)
                .build();
        Rabbit rabbit2 = Rabbit.builder()
                .status(Status.FOR_BREEDING)
                .sex(Sex.MALE)
                .build();
        Rabbit rabbit3 = Rabbit.builder()
                .status(Status.FOR_MEAT)
                .sex(Sex.MALE)
                .mother(rabbit)
                .birthDate(LocalDate.of(2024, 5, 23))
                .build();
        Rabbit rabbit4 = Rabbit.builder()
                .status(Status.FOR_MEAT)
                .sex(Sex.FEMALE)
                .father(rabbit2)
                .birthDate(LocalDate.of(2025, 5, 23))
                .build();
        Rabbit rabbit5 = Rabbit.builder()
                .status(Status.FOR_MEAT)
                .sex(Sex.MALE)
                .father(rabbit2)
                .birthDate(LocalDate.of(2025, 9, 29))
                .build();
        Rabbit rabbit6 = Rabbit.builder()
                .status(Status.PROCESSED)
                .build();

        double statusPercentage = RabbitryUtils.getStatusPercentage(List.of(rabbit, rabbit2, rabbit3, rabbit4, rabbit5, rabbit6), "FOR_MEAT");

        assertEquals(50, statusPercentage);
    }
}
