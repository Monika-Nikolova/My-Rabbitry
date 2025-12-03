package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.rabbit.model.*;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.user.model.UserRole;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.RabbitRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RabbitController.class)
public class RabbitControllerApiTest {

    @MockitoBean
    private RabbitService rabbitService;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<RabbitRequest> registerRequestArgumentCaptor;

    @Test
    void getMyRabbitsPage_shouldReturnMyRabbitsView_andStatusOk_andModelRabbits() throws Exception {

        when(rabbitService.getByOwnerId(any())).thenReturn(List.of(randomRabbit()));

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = get("/rabbits/me")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(view().name("my-rabbits"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("rabbits"));
    }

    @Test
    void getAddRabbitPage_shouldReturnAddRabbitView_andStatusOk_andModelRabbitRequest() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = get("/rabbits/new")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(view().name("add-rabbit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("rabbitRequest"));
    }

    @Test
    void createNewRabbit_shouldReturnStatus302_andRedirectToRabbitsMe_andInvokeCreateNewRabbitMethod() throws Exception {

        UUID userId = UUID.randomUUID();
        UserData authentication = new UserData(userId, "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = post("/rabbits/new")
                .with(user(authentication))
                .formField("code", "14-qw")
                .formField("sex", "MALE")
                .formField("colour", "Brown")
                .formField("pattern", "Aguti")
                .formField("eyeColour", "BROWN")
                .formField("earShape", "LOP")
                .formField("coatLength", "LONG")
                .formField("status", "FOR_MEAT")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rabbits/me"));

        verify(rabbitService).createNewRabbit(registerRequestArgumentCaptor.capture(), any());

        RabbitRequest rabbitRequest = registerRequestArgumentCaptor.getValue();
        assertEquals("14-qw", rabbitRequest.getCode());
        assertEquals(Sex.MALE, rabbitRequest.getSex());
        assertEquals("Brown", rabbitRequest.getColour());
        assertEquals("Aguti", rabbitRequest.getPattern());
        assertEquals(EyeColour.BROWN, rabbitRequest.getEyeColour());
        assertEquals(EarShape.LOP, rabbitRequest.getEarShape());
        assertEquals(CoatLength.LONG, rabbitRequest.getCoatLength());
        assertEquals(Status.FOR_MEAT, rabbitRequest.getStatus());
    }

    @Test
    void createNewRabbit_andBindingResultHasErrors_shouldReturnStatus200_andShowViewAddRabbit_andNotInvokeCreateNewRabbitMethod() throws Exception {

        UUID userId = UUID.randomUUID();
        UserData authentication = new UserData(userId, "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = post("/rabbits/new")
                .with(user(authentication))
                .formField("code", "14-qw")
                .formField("sex", "")
                .formField("colour", "   ")
                .formField("pattern", "Aguti")
                .formField("eyeColour", "BROWN")
                .formField("earShape", "LOP")
                .formField("coatLength", "LONG")
                .formField("status", "FOR_MEAT")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-rabbit"));
        verifyNoInteractions(rabbitService);
    }

    private static Rabbit randomRabbit() {
        return Rabbit.builder()
                .code("12-qw")
                .sex(Sex.FEMALE)
                .colour("white")
                .pattern("solid")
                .eyeColour(EyeColour.BLUE)
                .earShape(EarShape.UPRIGHT)
                .coatLength(CoatLength.MEDIUM)
                .status(Status.FOR_BREEDING)
                .build();
    }

    private static List<String> getDefaultPermissions() {
        return List.of("view_pregnancy_details",
                "create_pregnancy_details",
                "edit_pregnancy_details",
                "view_my_rabbits",
                "create_rabbits",
                "edit_rabbits");
    }
}
