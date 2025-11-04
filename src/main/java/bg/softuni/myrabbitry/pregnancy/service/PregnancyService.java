package bg.softuni.myrabbitry.pregnancy.service;

import bg.softuni.myrabbitry.pregnancy.repository.PregnancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PregnancyService {

    private final PregnancyRepository pregnancyRepository;

    @Autowired
    public PregnancyService(PregnancyRepository pregnancyRepository) {
        this.pregnancyRepository = pregnancyRepository;
    }
}
