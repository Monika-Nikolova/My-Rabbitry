package bg.softuni.myrabbitry.rabbit;

import bg.softuni.myrabbitry.exception.RabbitAlreadyExistsException;
import bg.softuni.myrabbitry.exception.RabbitWrongSexException;
import bg.softuni.myrabbitry.rabbit.model.*;
import bg.softuni.myrabbitry.rabbit.repository.RabbitRepository;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.EditProfileRequest;
import bg.softuni.myrabbitry.web.dto.RabbitRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RabbitServiceUTest {

    @Mock
    private RabbitRepository rabbitRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private RabbitService rabbitService;

    @Test
    void whenCreateNewRabbit_saveRabbitInDatabase() {

        UUID ownerId = UUID.randomUUID();
        Rabbit motherRabbit = Rabbit
                .builder()
                .sex(Sex.FEMALE)
                .build();
        Rabbit fatherRabbit = Rabbit
                .builder()
                .sex(Sex.MALE)
                .build();
        RabbitRequest rabbitRequest = RabbitRequest.builder()
                .code("12-qw")
                .sex(Sex.FEMALE)
                .colour("white")
                .pattern("solid")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .motherCode("13-qw")
                .fatherCode("14-qw")
                .build();
        User owner = new User();
        when(userService.getById(any())).thenReturn(owner);
        when(rabbitRepository.findByCodeAndOwner("12-qw", owner)).thenReturn(Optional.empty());
        when(rabbitRepository.findByCodeAndOwner("13-qw", owner)).thenReturn(Optional.of(motherRabbit));
        when(rabbitRepository.findByCodeAndOwner("14-qw", owner)).thenReturn(Optional.of(fatherRabbit));

        rabbitService.createNewRabbit(rabbitRequest, ownerId);

        verify(rabbitRepository).save(any());
    }

    @Test
    void whenCreateNewRabbit_andMotherHasWrongSex_ThrowException() {

        UUID ownerId = UUID.randomUUID();
        Rabbit motherRabbit = Rabbit
                .builder()
                .sex(Sex.MALE)
                .build();
        RabbitRequest rabbitRequest = RabbitRequest.builder()
                .code("12-qw")
                .sex(Sex.FEMALE)
                .colour("white")
                .pattern("solid")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .motherCode("13-qw")
                .fatherCode("14-qw")
                .build();
        User owner = new User();
        when(userService.getById(any())).thenReturn(owner);
        when(rabbitRepository.findByCodeAndOwner("12-qw", owner)).thenReturn(Optional.empty());
        when(rabbitRepository.findByCodeAndOwner("13-qw", owner)).thenReturn(Optional.of(motherRabbit));

        assertThrows(RabbitWrongSexException.class, () -> rabbitService.createNewRabbit(rabbitRequest, ownerId));
    }

    @Test
    void whenCreateNewRabbit_andFatherHasWrongSex_ThrowException() {

        UUID ownerId = UUID.randomUUID();
        Rabbit motherRabbit = Rabbit
                .builder()
                .sex(Sex.FEMALE)
                .build();
        Rabbit fatherRabbit = Rabbit
                .builder()
                .sex(Sex.FEMALE)
                .build();
        RabbitRequest rabbitRequest = RabbitRequest.builder()
                .code("12-qw")
                .sex(Sex.FEMALE)
                .colour("white")
                .pattern("solid")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .motherCode("13-qw")
                .fatherCode("14-qw")
                .build();
        User owner = new User();
        when(userService.getById(any())).thenReturn(owner);
        when(rabbitRepository.findByCodeAndOwner("12-qw", owner)).thenReturn(Optional.empty());
        when(rabbitRepository.findByCodeAndOwner("13-qw", owner)).thenReturn(Optional.of(motherRabbit));
        when(rabbitRepository.findByCodeAndOwner("14-qw", owner)).thenReturn(Optional.of(fatherRabbit));

        assertThrows(RabbitWrongSexException.class, () -> rabbitService.createNewRabbit(rabbitRequest, ownerId));
    }

    @Test
    void whenCreateNewRabbit_andRabbitWithThisCodeAlreadyExists_ThrowException() {

        UUID ownerId = UUID.randomUUID();
        RabbitRequest rabbitRequest = RabbitRequest.builder()
                .code("12-qw")
                .build();
        User owner = new User();
        when(userService.getById(any())).thenReturn(owner);
        when(rabbitRepository.findByCodeAndOwner("12-qw", owner)).thenReturn(Optional.of(new Rabbit()));

        assertThrows(RabbitAlreadyExistsException.class, () -> rabbitService.createNewRabbit(rabbitRequest, ownerId));
    }

    @Test
    void whenEditRabbit_thenPersistEditedRabbit() {

        UUID rabbitId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Rabbit rabbit = Rabbit.builder()
                .code("12-qw")
                .sex(Sex.FEMALE)
                .colour("white")
                .pattern("solid")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();

        RabbitRequest rabbitRequest = RabbitRequest.builder()
                .code("14-qw")
                .sex(Sex.MALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BROWN)
                .earShape(EarShape.LOP)
                .coatLength(CoatLength.LONG)
                .status(Status.FOR_MEAT)
                .fatherCode("")
                .motherCode("")
                .build();
        when(rabbitRepository.findById(any())).thenReturn(Optional.of(rabbit));

        rabbitService.editRabbit(rabbitId, rabbitRequest, ownerId);

        assertEquals(rabbitRequest.getCode(), rabbit.getCode());
        assertEquals(rabbitRequest.getSex(), rabbit.getSex());
        assertEquals(rabbitRequest.getColour(), rabbit.getColour());
        assertEquals(rabbitRequest.getPattern(), rabbit.getPattern());
        assertEquals(rabbitRequest.getEyeColour(), rabbit.getEyeColour());
        assertEquals(rabbitRequest.getEarShape(), rabbit.getEarShape());
        assertEquals(rabbitRequest.getCoatLength(), rabbit.getCoatLength());
        assertEquals(rabbitRequest.getStatus(), rabbit.getStatus());
        verify(rabbitRepository).save(rabbit);
    }
}
