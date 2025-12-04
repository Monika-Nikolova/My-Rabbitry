package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.rabbit.model.*;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.model.UserRole;
import bg.softuni.myrabbitry.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OverviewController.class)
public class OverviewControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PregnancyService pregnancyService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOverviewPage_shouldReturnStatusOk_andViewOverview_andModels() throws Exception {

        Rabbit rabbit = Rabbit.builder()
                .id(UUID.randomUUID())
                .code("13-qw")
                .sex(Sex.FEMALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BROWN)
                .earShape(EarShape.LOP)
                .coatLength(CoatLength.LONG)
                .status(Status.FOR_MEAT)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        Rabbit rabbit2 = Rabbit.builder()
                .id(UUID.randomUUID())
                .code("14-qw")
                .sex(Sex.MALE)
                .colour("Brown")
                .pattern("Aguti")
                .eyeColour(EyeColour.BROWN)
                .earShape(EarShape.LOP)
                .coatLength(CoatLength.LONG)
                .status(Status.FOR_MEAT)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        User user = User.builder()
                .rabbits(List.of(rabbit, rabbit2))
                .username("Monika1234")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isActive(true)
                .id(UUID.randomUUID())
                .password("123456")
                .role(UserRole.USER)
                .build();

        when(userService.getById(any())).thenReturn(user);
        when(pregnancyService.getBestFather(any())).thenReturn(null);
        when(pregnancyService.getBestMother(any())).thenReturn(null);

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = get("/overview")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("overview"))
                .andExpect(model().attributeExists("stats"))
                .andExpect(model().attributeExists("forMeat"));
        verify(userService).getById(any());
        verify(pregnancyService).getBestMother(any());
        verify(pregnancyService).getBestFather(any());
    }

    private static List<String> getLargeFarmPermissions() {
        return List.of("view_pregnancy_details",
                "create_pregnancy_details",
                "edit_pregnancy_details",
                "view_my_rabbits",
                "create_rabbits",
                "edit_rabbits",
                "view_maternity_ward",
                "view_overview");
    }
}
