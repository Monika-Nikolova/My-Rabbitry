package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.exception.UserAlreadyExistsException;
import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.model.UserRole;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PregnancyService pregnancyService;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<RegisterRequest> registerRequestArgumentCaptor;

    @Test
    void getIndexPage_shouldReturn200Ok_andIndexView() throws Exception {

        MockHttpServletRequestBuilder httpRequest = get("/");

        mockMvc.perform(httpRequest)
                .andExpect(view().name("index"))
                .andExpect(status().isOk());
    }

    @Test
    void postRegister_shouldReturn302_andRedirectToLogin_andInvokeRegisterServiceMethod() throws Exception {

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "Monika1234")
                .formField("password", "123456")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).register(registerRequestArgumentCaptor.capture());

        RegisterRequest dto = registerRequestArgumentCaptor.getValue();
        assertEquals("Monika1234", dto.getUsername());
        assertEquals("123456", dto.getPassword());
    }

    @Test
    void postRegisterWithUsernameThatAlreadyExists_shouldReturnOk_andRedirectToRegister() throws Exception {

        when(userService.register(any())).thenThrow(UserAlreadyExistsException.class);

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "Monika1234")
                .formField("password", "123456")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"));

        verify(userService).register(any());
    }

    @Test
    void postRegister_andUserServiceThrowsError_shouldReturnInternalServerError_andViewGeneralError() throws Exception {

        when(userService.register(any())).thenThrow(RuntimeException.class);

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "Monika1234")
                .formField("password", "123456")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("general-error"));

        verify(userService).register(any());
    }

    @Test
    void postRegisterWithInvalidFormData_shouldReturn200OkAndShowRegisterViewAndRegisterServiceMethodIsNeverInvoked() throws Exception {

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "M")
                .formField("password", "")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
        verify(userService, never()).register(any());
    }

    @Test
    void getDashboardPage_shouldReturnDashboardViewWithUserModel_andStatusCode200() throws Exception {

        Subscription subscription = Subscription.builder()
                .type(SubscriptionType.LARGE_FARM)
                .createdOn(LocalDateTime.now())
                .expirationOn(LocalDateTime.now().plusYears(1))
                .build();

        User user = User.builder()
                .rabbits(new ArrayList<>())
                .username("Monika1234")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .isActive(true)
                .id(UUID.randomUUID())
                .password("123456")
                .role(UserRole.USER)
                .subscriptions(List.of(subscription))
                .build();

        when(userService.getById(any())).thenReturn(user);
        when(pregnancyService.getLatest(any())).thenReturn(null);

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder httpRequest = get("/dashboard")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("user"));
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
