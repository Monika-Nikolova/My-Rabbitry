package bg.softuni.myrabbitry.pregnancy.service;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.repository.PregnancyRepository;
import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.rabbit.model.Sex;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.utils.PregnancyUtils;
import bg.softuni.myrabbitry.web.dto.BestParent;
import bg.softuni.myrabbitry.web.dto.PregnancyFilterRequest;
import bg.softuni.myrabbitry.web.dto.PregnancyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PregnancyService {

    private final PregnancyRepository pregnancyRepository;
    private final UserService userService;
    private final RabbitService rabbitService;

    @Autowired
    public PregnancyService(PregnancyRepository pregnancyRepository, UserService userService, RabbitService rabbitService) {
        this.pregnancyRepository = pregnancyRepository;
        this.userService = userService;
        this.rabbitService = rabbitService;
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
            int nullDeathPercentageCount = 0;
            int nullLactatingQuantityCount = 0;

            List<PregnancyReport> pregnancyReports = pregnancyRepository.getAllByMother(doe);
            for (PregnancyReport pregnancyReport : pregnancyReports) {
                if (!pregnancyReport.isFalsePregnancy() || !pregnancyReport.isWasPregnant()) {

                    if (pregnancyReport.getDeathPercentage() == null) {
                        nullDeathPercentageCount += 1;
                    } else {
                        totalDeathPercentage += pregnancyReport.getDeathPercentage();
                    }

                    if (pregnancyReport.getLactatingQuantity() == null) {
                        nullLactatingQuantityCount += 1;
                    } else {
                        totalLactatingQuantity += pregnancyReport.getLactatingQuantity();
                    }

                    totalPregnancies ++;
                    totalBornKids += pregnancyReport.getCountBornKids() != null ? pregnancyReport.getCountBornKids() : 0;
                    totalWeanedKids += pregnancyReport.getCountWeanedKids() != null ? pregnancyReport.getCountWeanedKids() : 0;
                }
            }
            double averageDeathPercentage = totalDeathPercentage / (pregnancyReports.size() - nullDeathPercentageCount);
            double averageLactatingQuantity = totalLactatingQuantity / (pregnancyReports.size() - nullLactatingQuantityCount);

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
        List<Rabbit> bucks = user.getRabbits().stream().filter(rabbit -> rabbit.getStatus().name().equals("FOR_BREEDING") && rabbit.getSex().name().equals("MALE")).toList();

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
                    totalKids += pregnancyReport.getCountBornKids() != null ? pregnancyReport.getCountBornKids() : 0;
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

    public List<PregnancyReport> getAllUpComingPregnanciesForUser(UUID id) {
        User user = userService.getById(id);
        List<PregnancyReport> pregnancies = pregnancyRepository.getAllByCreatedByOrderByDayOfFertilization(user);
        return pregnancies.stream().filter(pregnancy -> LocalDate.now().isBefore(pregnancy.getLatestDueDate())).toList();
    }

    public List<PregnancyReport> getAllPregnanciesForUser(UUID id) {
        User user = userService.getById(id);
        return pregnancyRepository.getAllByCreatedBy(user);
    }

    public void createPregnancyReport(PregnancyRequest pregnancyRequest, UUID id) {

        Rabbit father = null;
        if (!pregnancyRequest.getFather().isBlank()) {
            father = rabbitService.findByCode(pregnancyRequest.getFather());
            if (father.getSex() != Sex.MALE) {
                throw new RuntimeException("Father rabbit must be male");
            }
        }

        Rabbit mother = rabbitService.findByCode(pregnancyRequest.getMother());
        if (mother.getSex() != Sex.FEMALE) {
            throw new RuntimeException("Mother rabbit must be female");
        }

        LocalDate earliestDueDate = PregnancyUtils.calculateEarliestDueDate(pregnancyRequest.getDayOfFertilization());
        LocalDate latestDueDate = PregnancyUtils.calculateLatestDueDate(pregnancyRequest.getDayOfFertilization());
        Double deathPercentage = PregnancyUtils.calculateDeathPercentage(pregnancyRequest.getCountBornKids(), pregnancyRequest.getCountWeanedKids());
        Double lactatingQuantity = PregnancyUtils.calculateLactatingQuantity(pregnancyRequest.getTotalWeightKidsDay1(), pregnancyRequest.getTotalWeightKidsDay20());
        User user = userService.getById(id);

        PregnancyReport pregnancyReport = PregnancyReport.builder()
                .mother(mother)
                .father(father)
                .dayOfFertilization(pregnancyRequest.getDayOfFertilization())
                .earliestDueDate(earliestDueDate)
                .latestDueDate(latestDueDate)
                .dateOfBirth(pregnancyRequest.getDateOfBirth())
                .countBornKids(pregnancyRequest.getCountBornKids())
                .countWeanedKids(pregnancyRequest.getCountWeanedKids())
                .deathPercentage(deathPercentage)
                .totalWeightKidsDay1(pregnancyRequest.getTotalWeightKidsDay1())
                .totalWeightKidsDay20(pregnancyRequest.getTotalWeightKidsDay20())
                .isFalsePregnancy(pregnancyRequest.isFalsePregnancy())
                .isCannibalismPresent(pregnancyRequest.isCannibalismPresent())
                .hasAbort(pregnancyRequest.isHasAbort())
                .wasPregnant(pregnancyRequest.isWasPregnant())
                .lactatingQuantity(lactatingQuantity)
                .createdBy(user)
                .build();

        pregnancyRepository.save(pregnancyReport);
    }

    public PregnancyReport getById(UUID id) {
        return pregnancyRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("Pregnancy with id [%s] not found", id)));
    }

    public List<PregnancyReport> getFilteredAndSortedPregnanciesForUser(UUID id, PregnancyFilterRequest pregnancyFilterRequest) {

        List<PregnancyReport> pregnancyReports = sortPregnancies(pregnancyFilterRequest.getSortCriteria());

        if (!pregnancyFilterRequest.getFather().isBlank()) {
            Rabbit father = rabbitService.findByCode(pregnancyFilterRequest.getFather());
            pregnancyReports = pregnancyReports.stream().filter(pregnancy -> pregnancy.getFather().equals(father)).toList();

            if (!pregnancyFilterRequest.getMother().isBlank()) {
                Rabbit mother = rabbitService.findByCode(pregnancyFilterRequest.getMother());
                pregnancyReports = pregnancyReports.stream().filter(pregnancy -> pregnancy.getMother().equals(mother)).toList();
            }
        } else if (!pregnancyFilterRequest.getMother().isBlank()) {
            Rabbit mother = rabbitService.findByCode(pregnancyFilterRequest.getMother());
            pregnancyReports = pregnancyReports.stream().filter(pregnancy -> pregnancy.getMother().equals(mother)).toList();
        }

        return pregnancyReports;
    }

    private List<PregnancyReport> sortPregnancies(String sortCriteria) {
        return switch (sortCriteria) {
            case "DFD"-> pregnancyRepository.findAllByOrderByDayOfFertilizationDesc();
            case "DFA"-> pregnancyRepository.findAllByOrderByDayOfFertilizationAsc();
            case "BKD"-> pregnancyRepository.findAllByOrderByCountBornKidsDesc();
            case "BKA"-> pregnancyRepository.findAllByOrderByCountBornKidsAsc();
            default -> pregnancyRepository.findAll();
        };
    }
}
