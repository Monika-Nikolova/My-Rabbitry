package bg.softuni.myrabbitry.utils;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.web.dto.RabbitryStats;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.*;

@UtilityClass
public class RabbitryUtils {



    public static RabbitryStats getRabbitryStats(List<Rabbit> rabbits) {
        List<Rabbit> forBreeding = rabbits.stream().filter(rabbit -> rabbit.getStatus().name().equals("FOR_BREEDING")).toList();
        List<Rabbit> forMeat = rabbits.stream().filter(rabbit -> rabbit.getStatus().name().equals("FOR_MEAT")).toList();

        List<Rabbit> bucks = forBreeding.stream().filter(rabbit -> rabbit.getSex().name().equals("MALE")).toList();
        List<Rabbit> does = forBreeding.stream().filter(rabbit -> rabbit.getSex().name().equals("FEMALE")).toList();

        List<Rabbit> bornInRabbitry = rabbits.stream().filter(rabbit -> rabbit.getMother() != null || rabbit.getFather() != null).toList();

        Map<Integer, Integer> countRabbitsByYear = new TreeMap<>();
        bornInRabbitry.stream().filter(rabbit -> rabbit.getBirthDate() != null).forEach(rabbit -> {
            countRabbitsByYear.putIfAbsent(rabbit.getBirthDate().getYear(), 0);
            countRabbitsByYear.put(rabbit.getBirthDate().getYear(), countRabbitsByYear.get(rabbit.getBirthDate().getYear()) + 1);
        });

        Map<Integer, Integer> countRabbitsByYearLatestFive = new LinkedHashMap<>();
        int mostRabbitsInYear = 0;
        for (int i = LocalDate.now().getYear(); i > LocalDate.now().getYear() - 5; i--) {
            if (countRabbitsByYear.containsKey(i)) {
                countRabbitsByYearLatestFive.put(i, countRabbitsByYear.get(i));

                if (countRabbitsByYear.get(i) > mostRabbitsInYear) {
                    mostRabbitsInYear = countRabbitsByYear.get(i);
                }
            }
        }

        return RabbitryStats.builder()
                .rabbitsInRabbitry(forBreeding.size() + forMeat.size())
                .totalRabbits(rabbits.size())
                .bucks(bucks.size())
                .does(does.size())
                .bornInRabbitry(bornInRabbitry.size())
                .mostRabbitsInYear(mostRabbitsInYear)
                .countRabbitsByYear(countRabbitsByYearLatestFive)
                .build();
    }

    public static double getStatusPercentage(List<Rabbit> rabbits, String status) {
        if (rabbits.isEmpty()) {
            return 0;
        }
        return (rabbits.stream().filter(rabbit -> rabbit.getStatus().name().equals(status)).toList().size() * 1.0) / rabbits.size() * 100;
    }
}
