package bg.softuni.myrabbitry;

import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.rabbit.model.*;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.BestParent;
import bg.softuni.myrabbitry.web.dto.PregnancyRequest;
import bg.softuni.myrabbitry.web.dto.RabbitRequest;
import bg.softuni.myrabbitry.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class GetBestMotherITest {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private PregnancyService pregnancyService;

    @Autowired
    private UserService userService;

    @Test
    void getBestMother_withAllFieldsFilled() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Bolt")
                .password("123456")
                .build();
        User user = userService.register(registerRequest);

        RabbitRequest rabbitRequest = RabbitRequest.builder()
                .code("12-qw")
                .sex(Sex.FEMALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
        rabbitService.createNewRabbit(rabbitRequest, user.getId());

        RabbitRequest rabbitRequest2 = RabbitRequest.builder()
                .code("13-qw")
                .sex(Sex.FEMALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
        rabbitService.createNewRabbit(rabbitRequest2, user.getId());

        PregnancyRequest pregnancyRequest = PregnancyRequest.builder()
                .mother("12-qw")
                .dayOfFertilization(LocalDate.of(2025, 5, 18))
                .dateOfBirth(LocalDate.of(2025, 6, 18))
                .countBornKids(8)
                .countWeanedKids(7)
                .totalWeightKidsDay1(150.0)
                .totalWeightKidsDay20(1800.0)
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest, user.getId());

        PregnancyRequest pregnancyRequest2 = PregnancyRequest.builder()
                .mother("13-qw")
                .dayOfFertilization(LocalDate.of(2025, 5, 18))
                .dateOfBirth(LocalDate.of(2025, 6, 18))
                .countBornKids(9)
                .countWeanedKids(3)
                .totalWeightKidsDay1(150.0)
                .totalWeightKidsDay20(1000.0)
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest2, user.getId());

        BestParent bestMother = pregnancyService.getBestMother(user.getId());

        assertEquals("12-qw", bestMother.getCode());
        assertEquals(8, bestMother.getBornKids());
        assertEquals(1, bestMother.getCountLiters());
        assertEquals(12.5, bestMother.getDeathPercentage());
        assertEquals(3300, bestMother.getLactatingQuantity());
    }

    @Test
    void getBestMother_withOnlyMandatoryFieldsFilled() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Bolt")
                .password("123456")
                .build();
        User user = userService.register(registerRequest);

        RabbitRequest rabbitRequest = RabbitRequest.builder()
                .code("12-qw")
                .sex(Sex.FEMALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
        rabbitService.createNewRabbit(rabbitRequest, user.getId());

        RabbitRequest rabbitRequest2 = RabbitRequest.builder()
                .code("13-qw")
                .sex(Sex.FEMALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
        rabbitService.createNewRabbit(rabbitRequest2, user.getId());

        PregnancyRequest pregnancyRequest = PregnancyRequest.builder()
                .mother("12-qw")
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest, user.getId());

        PregnancyRequest pregnancyRequest2 = PregnancyRequest.builder()
                .mother("13-qw")
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest2, user.getId());

        BestParent bestMother = pregnancyService.getBestMother(user.getId());

        assertEquals("13-qw", bestMother.getCode());
        assertEquals(0, bestMother.getBornKids());
        assertNull(bestMother.getWeanedKids());
        assertEquals(1, bestMother.getCountLiters());
        assertEquals(0, bestMother.getDeathPercentage());
        assertEquals(-1, bestMother.getLactatingQuantity());
    }

    @Test
    void getBestMother_whenThereAreNoMothers() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Bolt")
                .password("123456")
                .build();
        User user = userService.register(registerRequest);

        BestParent bestMother = pregnancyService.getBestMother(user.getId());

        assertNull(bestMother);
    }
}
