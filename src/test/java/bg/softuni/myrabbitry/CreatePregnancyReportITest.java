package bg.softuni.myrabbitry;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.rabbit.model.*;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.PregnancyRequest;
import bg.softuni.myrabbitry.web.dto.RabbitRequest;
import bg.softuni.myrabbitry.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class CreatePregnancyReportITest {

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private PregnancyService pregnancyService;

    @Test
    void createPregnancyReport_happyPath() {

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

        PregnancyRequest pregnancyRequest = PregnancyRequest.builder()
                .mother("12-qw")
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest, user.getId());

        List<PregnancyReport> pregnancies = pregnancyService.getAllPregnanciesForUser(user.getId());
        assertThat(pregnancies).hasSize(1);
        PregnancyReport pregnancyReport = pregnancies.stream().findFirst().get();
        assertEquals("12-qw",  pregnancyReport.getMother().getCode());
        assertFalse(pregnancyReport.isFalsePregnancy());
        assertFalse(pregnancyReport.isCannibalismPresent());
        assertFalse(pregnancyReport.isHasAbort());
        assertTrue(pregnancyReport.isWasPregnant());
    }
}
