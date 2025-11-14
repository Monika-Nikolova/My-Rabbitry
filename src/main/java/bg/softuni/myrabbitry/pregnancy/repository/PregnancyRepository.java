package bg.softuni.myrabbitry.pregnancy.repository;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PregnancyRepository extends JpaRepository<PregnancyReport, UUID> {
    List<PregnancyReport> getAllByMotherInOrderByDayOfFertilizationDesc(Collection<Rabbit> mother);

    List<PregnancyReport> getAllByMother(Rabbit doe);

    List<PregnancyReport> getAllByFather(Rabbit buck);

    List<PregnancyReport> getAllByCreatedByOrderByDayOfFertilization(User user);

    List<PregnancyReport> getAllByCreatedBy(User user);

    List<PregnancyReport> findAllByOrderByDayOfFertilizationDesc();

    List<PregnancyReport> findAllByOrderByDayOfFertilizationAsc();

    List<PregnancyReport> findAllByOrderByCountBornKidsDesc();

    List<PregnancyReport> findAllByOrderByCountBornKidsAsc();
}
