package bg.softuni.myrabbitry.rabbit.service;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.rabbit.repository.RabbitRepository;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
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

        Optional<Rabbit> optionalRabbit = rabbitRepository.findByCode(rabbitRequest.getCode());

        if (optionalRabbit.isPresent()) {
            throw new RuntimeException(String.format("Rabbit with code %s already exists", rabbitRequest.getCode()));
        }

        Rabbit mother = null;
        if (!rabbitRequest.getMotherCode().isBlank()) {
            mother = this.findByCode(rabbitRequest.getMotherCode());
        }

        Rabbit father = null;
        if (!rabbitRequest.getFatherCode().isBlank()) {
            father = this.findByCode(rabbitRequest.getFatherCode());
        }

        User owner = userService.getById(id);

        Rabbit rabbit = Rabbit.builder()
                .photoUrl(rabbitRequest.getPhotoUrl())
                .name(rabbitRequest.getName())
                .code(rabbitRequest.getCode())
                .description(rabbitRequest.getDescription())
                .mother(mother)
                .father(father)
                .birthDate(rabbitRequest.getBirthDate())
                .sex(rabbitRequest.getSex())
                .colour(rabbitRequest.getColour())
                .pattern(rabbitRequest.getPattern())
                .eyeColour(rabbitRequest.getEyeColour())
                .earShape(rabbitRequest.getEarShape())
                .coatLength(rabbitRequest.getCoatLength())
                .breed(rabbitRequest.getBreed())
                .vaccinatedOn(rabbitRequest.getVaccinatedOn())
                .status(rabbitRequest.getStatus())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .owner(owner)
                .build();

        rabbitRepository.save(rabbit);

        log.info(String.format("Rabbit with id [%s] and code [%s] has been created", rabbit.getId(), rabbitRequest.getCode()));
    }

    public Rabbit findByCode(String code) {
        return rabbitRepository.findByCode(code).orElseThrow(() -> new RuntimeException(String.format("Rabbit with code %s not found", code)));
    }

    public FamilyTreeDto createFamilyTree(String code) {
        return buildTree(findByCode(code), 4);
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
}
