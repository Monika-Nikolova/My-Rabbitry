package bg.softuni.myrabbitry.pregnancy.service;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.repository.PregnancyRepository;
import bg.softuni.myrabbitry.rabbit.model.Rabbit;
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
}
