package bg.softuni.myrabbitry.pregnancy.service;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.repository.PregnancyRepository;
import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.rabbit.model.Sex;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.utils.PregnancyUtils;
import bg.softuni.myrabbitry.web.dto.BestParent;
import bg.softuni.myrabbitry.web.dto.PregnancyFilterRequest;
import bg.softuni.myrabbitry.web.dto.PregnancyRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PregnancyService {

    private final PregnancyRepository pregnancyRepository;
    private final RabbitService rabbitService;

    @Autowired
    public PregnancyService(PregnancyRepository pregnancyRepository, RabbitService rabbitService) {
        this.pregnancyRepository = pregnancyRepository;
        this.rabbitService = rabbitService;
    }

    @Cacheable("latestPregnancy")
    public PregnancyReport getLatest(List<Rabbit> rabbits, UUID id) {
        List<PregnancyReport> pregnancyReports = pregnancyRepository.getAllByMotherInOrderByDayOfFertilizationDesc(rabbits);

        if (pregnancyReports.isEmpty()) {
            return null;
        }

        return pregnancyReports.get(0);
    }

    @Cacheable("bestMother")
    public BestParent getBestMother(User user) {
        List<Rabbit> does = user.getRabbits().stream().filter(rabbit -> rabbit.getStatus().name().equals("FOR_BREEDING") && rabbit.getSex().name().equals("FEMALE")).toList();

        if (does.isEmpty()) {
            return null;
        }

        double smallestDeathPercentage = 101;
        double bestLactatingQuantity = 0;
        int pregnancyCount = 0;
        int bornKids = 0;
        Integer weanedKids = 0;
        Rabbit bestMother = null;
        for (Rabbit doe : does) {
            double totalLactatingQuantity = 0;
            double totalDeathPercentage = 0;
            int totalPregnancies = 0;
            int totalBornKids = 0;
            int totalWeanedKids = 0;
            int nullDeathPercentageCount = 0;
            int nullLactatingQuantityCount = 0;
            int nullWeanedKidsCount = 0;

            List<PregnancyReport> pregnancyReports = pregnancyRepository.getAllByMother(doe);
            for (PregnancyReport pregnancyReport : pregnancyReports) {
                if (!pregnancyReport.isFalsePregnancy() || !pregnancyReport.isCannibalismPresent()) {

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

                    if (pregnancyReport.getCountWeanedKids() == null) {
                        nullWeanedKidsCount += 1;
                    } else {
                        totalWeanedKids += pregnancyReport.getCountWeanedKids();
                    }
                }
            }

            double averageDeathPercentage = 0;
            if (pregnancyReports.size() - nullDeathPercentageCount != 0) {
                averageDeathPercentage = totalDeathPercentage / (pregnancyReports.size() - nullDeathPercentageCount);
            }
            double averageLactatingQuantity = totalLactatingQuantity / (pregnancyReports.size() - nullLactatingQuantityCount);

            if (averageDeathPercentage < smallestDeathPercentage || averageDeathPercentage == smallestDeathPercentage && averageLactatingQuantity > bestLactatingQuantity) {
                smallestDeathPercentage = averageDeathPercentage;
                bestLactatingQuantity = averageLactatingQuantity;
                pregnancyCount = totalPregnancies;
                bornKids = totalBornKids;
                if (pregnancyReports.size() == nullWeanedKidsCount) {
                    weanedKids = null;
                } else {
                    weanedKids = totalWeanedKids;
                }
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
                .lactatingQuantity(Double.isNaN(bestLactatingQuantity) ? -1 : bestLactatingQuantity)
                .build();
    }

    @Cacheable("bestFather")
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
                if (!pregnancyReport.isFalsePregnancy() || !pregnancyReport.isCannibalismPresent()) {
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

    @Cacheable("upComingPregnancies")
    public List<PregnancyReport> getAllUpComingPregnanciesForUser(UUID id) {
        List<PregnancyReport> pregnancies = getAllPregnanciesForUser(id);
        return pregnancies.stream().filter(pregnancy -> pregnancy.getDateOfBirth() == null || LocalDate.now().isBefore(pregnancy.getLatestDueDate())).toList();
    }

    @Cacheable("pregnancies")
    public List<PregnancyReport> getAllPregnanciesForUser(UUID id) {
        List<Rabbit> rabbits = rabbitService.getByOwnerId(id);
        return pregnancyRepository.getAllByMotherInOrFatherIn(rabbits, rabbits);
    }

    @CacheEvict(value = {"latestPregnancy", "pregnancies", "upComingPregnancies", "bestMother", "bestFather"}, allEntries = true)
    public void createPregnancyReport(PregnancyRequest pregnancyRequest, UUID id) {

        Rabbit father = checkFatherMale(pregnancyRequest, id);

        Rabbit mother = checkMotherFemale(pregnancyRequest, id);

        PregnancyReport pregnancyReport = PregnancyReport.builder()
                .mother(mother)
                .father(father)
                .dayOfFertilization(pregnancyRequest.getDayOfFertilization())
                .dateOfBirth(pregnancyRequest.getDateOfBirth())
                .countBornKids(pregnancyRequest.getCountBornKids())
                .countWeanedKids(pregnancyRequest.getCountWeanedKids())
                .totalWeightKidsDay1(pregnancyRequest.getTotalWeightKidsDay1())
                .totalWeightKidsDay20(pregnancyRequest.getTotalWeightKidsDay20())
                .isFalsePregnancy(pregnancyRequest.isFalsePregnancy())
                .isCannibalismPresent(pregnancyRequest.isCannibalismPresent())
                .hasAbort(pregnancyRequest.isHasAbort())
                .wasPregnant(pregnancyRequest.isWasPregnant())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        setCalculatedFields(pregnancyRequest, pregnancyReport);

        pregnancyRepository.save(pregnancyReport);
    }

    public PregnancyReport getById(UUID id) {
        return pregnancyRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("Pregnancy with id [%s] not found", id)));
    }

    public List<PregnancyReport> getFilteredAndSortedPregnanciesForUser(UUID id, PregnancyFilterRequest pregnancyFilterRequest) {

        List<PregnancyReport> pregnancyReports = sortPregnancies(pregnancyFilterRequest.getSortCriteria());

        if (!pregnancyFilterRequest.getFather().isBlank()) {
            Rabbit father = rabbitService.findByCode(pregnancyFilterRequest.getFather(), id);
            pregnancyReports = pregnancyReports.stream().filter(pregnancy -> pregnancy.getFather().equals(father)).toList();

            if (!pregnancyFilterRequest.getMother().isBlank()) {
                Rabbit mother = rabbitService.findByCode(pregnancyFilterRequest.getMother(), id);
                pregnancyReports = pregnancyReports.stream().filter(pregnancy -> pregnancy.getMother().equals(mother)).toList();
            }
        } else if (!pregnancyFilterRequest.getMother().isBlank()) {
            Rabbit mother = rabbitService.findByCode(pregnancyFilterRequest.getMother(), id);
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

    @CacheEvict(value = {"latestPregnancy", "pregnancies", "upComingPregnancies", "bestMother", "bestFather"}, allEntries = true)
    public void editPregnancy(UUID id, @Valid PregnancyRequest pregnancyRequest, UUID userId) {

        Rabbit father = checkFatherMale(pregnancyRequest, userId);

        Rabbit mother = checkMotherFemale(pregnancyRequest, userId);

        PregnancyReport pregnancyReport = getById(id);

        pregnancyReport.setMother(mother);
        pregnancyReport.setFather(father);
        pregnancyReport.setDayOfFertilization(pregnancyRequest.getDayOfFertilization());
        pregnancyReport.setDateOfBirth(pregnancyRequest.getDateOfBirth());
        pregnancyReport.setCountBornKids(pregnancyRequest.getCountBornKids());
        pregnancyReport.setCountWeanedKids(pregnancyRequest.getCountWeanedKids());
        pregnancyReport.setTotalWeightKidsDay1(pregnancyRequest.getTotalWeightKidsDay1());
        pregnancyReport.setTotalWeightKidsDay20(pregnancyRequest.getTotalWeightKidsDay20());
        pregnancyReport.setFalsePregnancy(pregnancyRequest.isFalsePregnancy());
        pregnancyReport.setCannibalismPresent(pregnancyRequest.isCannibalismPresent());
        pregnancyReport.setHasAbort(pregnancyRequest.isHasAbort());
        pregnancyReport.setWasPregnant(pregnancyRequest.isWasPregnant());
        pregnancyReport.setUpdatedOn(LocalDateTime.now());

        setCalculatedFields(pregnancyRequest, pregnancyReport);

        pregnancyRepository.save(pregnancyReport);
    }

    private void setCalculatedFields(PregnancyRequest pregnancyRequest, PregnancyReport pregnancyReport) {
        LocalDate earliestDueDate = PregnancyUtils.calculateEarliestDueDate(pregnancyRequest.getDayOfFertilization());
        LocalDate latestDueDate = PregnancyUtils.calculateLatestDueDate(pregnancyRequest.getDayOfFertilization());
        Double deathPercentage = PregnancyUtils.calculateDeathPercentage(pregnancyRequest.getCountBornKids(), pregnancyRequest.getCountWeanedKids());
        Double lactatingQuantity = PregnancyUtils.calculateLactatingQuantity(pregnancyRequest.getTotalWeightKidsDay1(), pregnancyRequest.getTotalWeightKidsDay20());

        pregnancyReport.setEarliestDueDate(earliestDueDate);
        pregnancyReport.setLatestDueDate(latestDueDate);
        pregnancyReport.setDeathPercentage(deathPercentage);
        pregnancyReport.setLactatingQuantity(lactatingQuantity);
    }

    private Rabbit checkFatherMale(PregnancyRequest pregnancyRequest, UUID userId) {
        Rabbit father = null;
        if (!pregnancyRequest.getFather().isBlank()) {
            father = rabbitService.findByCode(pregnancyRequest.getFather(), userId);
            if (father.getSex() != Sex.MALE) {
                throw new RuntimeException("Father rabbit must be male");
            }
        }
        return father;
    }

    private Rabbit checkMotherFemale(PregnancyRequest pregnancyRequest, UUID id) {
        Rabbit mother = rabbitService.findByCode(pregnancyRequest.getMother(), id);
        if (mother.getSex() != Sex.FEMALE) {
            throw new RuntimeException("Mother rabbit must be female");
        }
        return mother;
    }
}
