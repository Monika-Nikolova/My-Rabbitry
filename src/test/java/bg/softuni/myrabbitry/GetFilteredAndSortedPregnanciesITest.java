package bg.softuni.myrabbitry;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.rabbit.model.*;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.PregnancyFilterRequest;
import bg.softuni.myrabbitry.web.dto.PregnancyRequest;
import bg.softuni.myrabbitry.web.dto.RabbitRequest;
import bg.softuni.myrabbitry.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class GetFilteredAndSortedPregnanciesITest {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private PregnancyService pregnancyService;

    @Autowired
    private UserService userService;

    @Test
    void getFilteredAndSortedPregnanciesForUser_withMotherAndSortCriteria_thenReturnSortedAndFilteredPregnancies() {

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
                .dayOfFertilization(LocalDate.of(2021, 1, 1))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest, user.getId());

        PregnancyRequest pregnancyRequest2 = PregnancyRequest.builder()
                .mother("12-qw")
                .dayOfFertilization(LocalDate.of(2025, 5, 15))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest2, user.getId());

        PregnancyRequest pregnancyRequest3 = PregnancyRequest.builder()
                .mother("13-qw")
                .dayOfFertilization(LocalDate.of(2023, 7, 26))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest3, user.getId());

        PregnancyFilterRequest filterRequest = PregnancyFilterRequest.builder()
                .mother("12-qw")
                .sortCriteria("DFD")
                .build();

        List<PregnancyReport> filteredAndSortedPregnancies = pregnancyService.getFilteredAndSortedPregnanciesForUser(user.getId(), filterRequest);

        assertThat(filteredAndSortedPregnancies).hasSize(2);
        assertEquals("12-qw", filteredAndSortedPregnancies.get(0).getMother().getCode());
        assertEquals(LocalDate.of(2025, 5, 15), filteredAndSortedPregnancies.get(0).getDayOfFertilization());
        assertEquals("12-qw", filteredAndSortedPregnancies.get(1).getMother().getCode());
        assertEquals(LocalDate.of(2021, 1, 1), filteredAndSortedPregnancies.get(1).getDayOfFertilization());
    }

    @Test
    void getFilteredAndSortedPregnanciesForUser_withMotherAndFatherAndSortCriteria_thenReturnSortedAndFilteredPregnancies() {

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
                .dayOfFertilization(LocalDate.of(2021, 1, 1))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest, user.getId());

        PregnancyRequest pregnancyRequest2 = PregnancyRequest.builder()
                .mother("12-qw")
                .dayOfFertilization(LocalDate.of(2025, 5, 15))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest2, user.getId());

        PregnancyRequest pregnancyRequest3 = PregnancyRequest.builder()
                .mother("13-qw")
                .dayOfFertilization(LocalDate.of(2023, 7, 26))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest3, user.getId());

        RabbitRequest rabbitRequest3 = RabbitRequest.builder()
                .code("14-qw")
                .sex(Sex.MALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
        rabbitService.createNewRabbit(rabbitRequest3, user.getId());

        PregnancyRequest pregnancyRequest4 = PregnancyRequest.builder()
                .mother("12-qw")
                .father("14-qw")
                .dayOfFertilization(LocalDate.of(2022, 7, 26))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest4, user.getId());

        PregnancyFilterRequest filterRequest = PregnancyFilterRequest.builder()
                .mother("12-qw")
                .father("14-qw")
                .sortCriteria("DFA")
                .build();

        List<PregnancyReport> filteredAndSortedPregnancies = pregnancyService.getFilteredAndSortedPregnanciesForUser(user.getId(), filterRequest);

        assertThat(filteredAndSortedPregnancies).hasSize(1);
        assertEquals("12-qw", filteredAndSortedPregnancies.get(0).getMother().getCode());
        assertEquals("14-qw", filteredAndSortedPregnancies.get(0).getFather().getCode());
        assertEquals(LocalDate.of(2022, 7, 26), filteredAndSortedPregnancies.get(0).getDayOfFertilization());
    }

    @Test
    void getFilteredAndSortedPregnanciesForUser_withFatherAndSortCriteria_thenReturnSortedAndFilteredPregnancies() {

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
                .dayOfFertilization(LocalDate.of(2021, 1, 1))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest, user.getId());

        PregnancyRequest pregnancyRequest2 = PregnancyRequest.builder()
                .mother("12-qw")
                .dayOfFertilization(LocalDate.of(2025, 5, 15))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest2, user.getId());

        PregnancyRequest pregnancyRequest3 = PregnancyRequest.builder()
                .mother("13-qw")
                .dayOfFertilization(LocalDate.of(2023, 7, 26))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest3, user.getId());

        RabbitRequest rabbitRequest3 = RabbitRequest.builder()
                .code("14-qw")
                .sex(Sex.MALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
        rabbitService.createNewRabbit(rabbitRequest3, user.getId());

        PregnancyRequest pregnancyRequest4 = PregnancyRequest.builder()
                .mother("12-qw")
                .father("14-qw")
                .dayOfFertilization(LocalDate.of(2022, 7, 26))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest4, user.getId());

        PregnancyRequest pregnancyRequest5 = PregnancyRequest.builder()
                .mother("13-qw")
                .father("14-qw")
                .dayOfFertilization(LocalDate.of(2025, 7, 26))
                .isFalsePregnancy(false)
                .isCannibalismPresent(false)
                .hasAbort(false)
                .wasPregnant(true)
                .build();
        pregnancyService.createPregnancyReport(pregnancyRequest5, user.getId());

        PregnancyFilterRequest filterRequest = PregnancyFilterRequest.builder()
                .father("14-qw")
                .sortCriteria("DFA")
                .build();

        List<PregnancyReport> filteredAndSortedPregnancies = pregnancyService.getFilteredAndSortedPregnanciesForUser(user.getId(), filterRequest);

        assertThat(filteredAndSortedPregnancies).hasSize(2);
        assertEquals("12-qw", filteredAndSortedPregnancies.get(0).getMother().getCode());
        assertEquals("14-qw", filteredAndSortedPregnancies.get(0).getFather().getCode());
        assertEquals(LocalDate.of(2022, 7, 26), filteredAndSortedPregnancies.get(0).getDayOfFertilization());
        assertEquals("13-qw", filteredAndSortedPregnancies.get(1).getMother().getCode());
        assertEquals("14-qw", filteredAndSortedPregnancies.get(1).getFather().getCode());
        assertEquals(LocalDate.of(2025, 7, 26), filteredAndSortedPregnancies.get(1).getDayOfFertilization());
    }
}
