package bg.softuni.myrabbitry.rabbit.service;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.rabbit.model.Sex;
import bg.softuni.myrabbitry.rabbit.repository.RabbitRepository;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.DtoMapper;
import bg.softuni.myrabbitry.web.dto.FamilyTreeDto;
import bg.softuni.myrabbitry.web.dto.RabbitRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RabbitService {

    private final RabbitRepository rabbitRepository;
    private final UserService userService;

    @Autowired
    public RabbitService(RabbitRepository rabbitRepository, UserService userService) {
        this.rabbitRepository = rabbitRepository;
        this.userService = userService;
    }

    public List<Rabbit> getByOwnerId(UUID id) {
        User owner = userService.getById(id);
        return rabbitRepository.getByOwnerOrderByCreatedOnDesc(owner);
    }

    public void createNewRabbit(RabbitRequest rabbitRequest, UUID id) {

        User owner = userService.getById(id);

        checkRabbitIsPresent(rabbitRequest, owner);

        Rabbit mother = checkMotherFemale(rabbitRequest, id);

        Rabbit father = checkFatherMale(rabbitRequest, id);

        Rabbit rabbit = DtoMapper.fromRabbitRequest(rabbitRequest, mother, father, owner);

        rabbitRepository.save(rabbit);

        log.info(String.format("Rabbit with id [%s] and code [%s] has been created", rabbit.getId(), rabbitRequest.getCode()));
    }

    public Rabbit findByCode(String code, UUID ownerId) {
        return rabbitRepository.findByCodeAndOwner(code, userService.getById(ownerId)).orElseThrow(() -> new RuntimeException(String.format("Rabbit with code %s not found", code)));
    }

    public FamilyTreeDto createFamilyTree(String code, UUID id) {
        return buildTree(findByCode(code, id), 4);
    }

    public Rabbit getById(UUID id) {
        return rabbitRepository.findById(id).orElseThrow(() -> new RuntimeException(String.format("Rabbit with id %s not found", id)));
    }

    public void editRabbit(UUID id, RabbitRequest rabbitRequest, UUID ownerId) {

        Rabbit mother = checkMotherFemale(rabbitRequest, ownerId);

        Rabbit father = checkFatherMale(rabbitRequest, ownerId);

        Rabbit rabbit = getById(id);

        rabbit.setPhotoUrl(rabbitRequest.getPhotoUrl());
        rabbit.setName(rabbitRequest.getName());
        rabbit.setCode(rabbitRequest.getCode());
        rabbit.setDescription(rabbitRequest.getDescription());
        rabbit.setMother(mother);
        rabbit.setFather(father);
        rabbit.setBirthDate(rabbitRequest.getBirthDate());
        rabbit.setSex(rabbitRequest.getSex());
        rabbit.setColour(rabbitRequest.getColour());
        rabbit.setPattern(rabbitRequest.getPattern());
        rabbit.setEyeColour(rabbitRequest.getEyeColour());
        rabbit.setEarShape(rabbitRequest.getEarShape());
        rabbit.setCoatLength(rabbitRequest.getCoatLength());
        rabbit.setBreed(rabbitRequest.getBreed());
        rabbit.setVaccinatedOn(rabbitRequest.getVaccinatedOn());
        rabbit.setStatus(rabbitRequest.getStatus());
        rabbit.setUpdatedOn(LocalDateTime.now());

        rabbitRepository.save(rabbit);
    }

    private FamilyTreeDto buildTree(Rabbit rabbit, int generations) {
        if (rabbit == null || generations == 0) {
            return null;
        }
        return FamilyTreeDto.builder()
                .child(rabbit)
                .mother(buildTree(rabbit.getMother(), generations - 1))
                .father(buildTree(rabbit.getFather(), generations - 1))
                .build();
    }

    private Rabbit checkFatherMale(RabbitRequest rabbitRequest, UUID id) {
        Rabbit father = null;
        if (!rabbitRequest.getFatherCode().isBlank()) {
            father = findByCode(rabbitRequest.getFatherCode(), id);
            if (father.getSex() != Sex.MALE) {
                throw new RuntimeException("Father rabbit must be male");
            }
        }
        return father;
    }

    private Rabbit checkMotherFemale(RabbitRequest rabbitRequest, UUID id) {
        Rabbit mother = null;
        if (!rabbitRequest.getMotherCode().isBlank()) {
            mother = findByCode(rabbitRequest.getMotherCode(), id);
            if (mother.getSex() != Sex.FEMALE) {
                throw new RuntimeException("Mother rabbit must be female");
            }
        }
        return mother;
    }

    private void checkRabbitIsPresent(RabbitRequest rabbitRequest, User owner) {
        Optional<Rabbit> optionalRabbit = rabbitRepository.findByCodeAndOwner(rabbitRequest.getCode(), owner);

        if (optionalRabbit.isPresent()) {
            throw new RuntimeException(String.format("Rabbit with code %s already exists", rabbitRequest.getCode()));
        }
    }
}
