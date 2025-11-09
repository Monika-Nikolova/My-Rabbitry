package bg.softuni.myrabbitry.pregnancy.service;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.repository.PregnancyRepository;
import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.web.dto.BestParent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PregnancyService {

    private final PregnancyRepository pregnancyRepository;

    @Autowired
    public PregnancyService(PregnancyRepository pregnancyRepository) {
        this.pregnancyRepository = pregnancyRepository;
    }

    public PregnancyReport getLatest(List<Rabbit> rabbits, UUID id) {
        List<PregnancyReport> pregnancyReports = pregnancyRepository.getAllByMotherInOrderByDayOfFertilizationDesc(rabbits);

        if (pregnancyReports.isEmpty()) {
            return null;
        }

        return pregnancyReports.get(0);
    }

    public BestParent getBestMother(User user) {
        List<Rabbit> does = user.getRabbits().stream().filter(rabbit -> rabbit.getStatus().name().equals("FOR_BREEDING") && rabbit.getSex().name().equals("FEMALE")).toList();

        if (does.isEmpty()) {
            return null;
        }

        double smallestDeathPercentage = 101;
        double bestLactatingQuantity = 0;
        int pregnancyCount = 0;
        int bornKids = 0;
        int weanedKids = 0;
        Rabbit bestMother = null;
        for (Rabbit doe : does) {
            double totalLactatingQuantity = 0;
            double totalDeathPercentage = 0;
            int totalPregnancies = 0;
            int totalBornKids = 0;
            int totalWeanedKids = 0;

            List<PregnancyReport> pregnancyReports = pregnancyRepository.getAllByMother(doe);
            for (PregnancyReport pregnancyReport : pregnancyReports) {
                if (!pregnancyReport.isFalsePregnancy() || !pregnancyReport.isWasPregnant()) {
                    totalDeathPercentage += pregnancyReport.getDeathPercentage();
                    totalLactatingQuantity += pregnancyReport.getLactatingQuantity();
                    totalPregnancies ++;
                    totalBornKids += pregnancyReport.getCountBornKids();
                    totalWeanedKids += pregnancyReport.getCountWeanedKids();
                }
            }
            double averageDeathPercentage = totalDeathPercentage / pregnancyReports.size();
            double averageLactatingQuantity = totalLactatingQuantity / pregnancyReports.size();

            if (averageDeathPercentage < smallestDeathPercentage || averageDeathPercentage == smallestDeathPercentage && averageLactatingQuantity > bestLactatingQuantity) {
                smallestDeathPercentage = averageDeathPercentage;
                bestLactatingQuantity = averageLactatingQuantity;
                pregnancyCount = totalPregnancies;
                bornKids = totalBornKids;
                weanedKids = totalWeanedKids;
                bestMother = doe;
            }
        }

        if (bestMother == null) {
            return null;
        }

        return BestParent.builder()
                .code(bestMother.getCode())
                .name(bestMother.getName())
                .id(bestMother.getId())
                .birthDate(bestMother.getBirthDate())
                .countLiters(pregnancyCount)
                .bornKids(bornKids)
                .weanedKids(weanedKids)
                .deathPercentage(smallestDeathPercentage)
                .lactatingQuantity(bestLactatingQuantity)
                .build();
    }

    public BestParent getBestFather(User user) {
        List<Rabbit> bucks = user.getRabbits().stream().filter(rabbit -> rabbit.getStatus().name().equals("FOR_BREEDING") && rabbit.getSex().name().equals("Male")).toList();

        if (bucks.isEmpty()) {
            return null;
        }

        int mostPregnancies = 0;
        int mostKids = 0;
        Rabbit bestFather = null;
        for (Rabbit buck : bucks) {
            int totalPregnancies = 0;
            int totalKids = 0;

            List<PregnancyReport> pregnancyReports = pregnancyRepository.getAllByFather(buck);
            for (PregnancyReport pregnancyReport : pregnancyReports) {
                if (!pregnancyReport.isFalsePregnancy() || !pregnancyReport.isWasPregnant()) {
                    totalKids += pregnancyReport.getCountBornKids();
                    totalPregnancies ++;
                }
            }

            if (mostPregnancies < totalPregnancies || mostPregnancies == totalPregnancies && totalKids > mostKids) {
                mostPregnancies = totalPregnancies;
                mostKids = totalKids;
                bestFather = buck;
            }
        }

        if (bestFather == null) {
            return null;
        }

        return BestParent.builder()
                .code(bestFather.getCode())
                .name(bestFather.getName())
                .id(bestFather.getId())
                .birthDate(bestFather.getBirthDate())
                .bornKids(mostKids)
                .build();
    }
}
