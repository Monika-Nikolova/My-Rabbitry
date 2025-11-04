package bg.softuni.myrabbitry.pregnancy.repository;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PregnancyRepository extends JpaRepository<PregnancyReport, UUID> {
}
