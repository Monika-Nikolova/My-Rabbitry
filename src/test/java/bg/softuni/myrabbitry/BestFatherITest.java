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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class BestFatherITest {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private PregnancyService pregnancyService;

    @Autowired
    private UserService userService;

    @Test
    void getBestMother_withOneMaleWithMoreLitters_andOtherMaleWithLessLittersButMoreKids_thenReturnMaleWithMoreLitters() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Bolt")
                .password("123456")
                .build();
        User user = userService.register(registerRequest);

        RabbitRequest rabbitRequest = RabbitRequest.builder()
                .code("11-qw")
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
                .code("12-qw")
                .sex(Sex.MALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
        rabbitService.createNewRabbit(rabbitRequest2, user.getId());

        RabbitRequest rabbitRequest3 = RabbitRequest.builder()
                .code("13-qw")
                .sex(Sex.MALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
        rabbitService.createNewRabbit(rabbitRequest3, user.getId());

        PregnancyRequest pregnancyRequest = PregnancyRequest.builder()
                .mother("11-qw")
                .father("12-qw")
                .countBornKids(3)
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest, user.getId());

        PregnancyRequest pregnancyRequest2 = PregnancyRequest.builder()
                .mother("11-qw")
                .father("12-qw")
                .countBornKids(4)
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest2, user.getId());

        PregnancyRequest pregnancyRequest3 = PregnancyRequest.builder()
                .mother("11-qw")
                .father("13-qw")
                .countBornKids(9)
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest3, user.getId());

        BestParent bestFather = pregnancyService.getBestFather(user.getId());

        assertEquals("12-qw", bestFather.getCode());
        assertEquals(7, bestFather.getBornKids());
        assertEquals(2, bestFather.getCountLiters());
    }

    @Test
    void getBestMother_whenThereAreNoMothers() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Bolt")
                .password("123456")
                .build();
        User user = userService.register(registerRequest);

        BestParent bestFather = pregnancyService.getBestFather(user.getId());

        assertNull(bestFather);
    }
}
