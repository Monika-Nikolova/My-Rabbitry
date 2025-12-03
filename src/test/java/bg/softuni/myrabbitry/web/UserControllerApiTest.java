package bg.softuni.myrabbitry.web;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void patchRequestToChangeUserStatus_fromAdminUser_shouldReturnRedirectAndInvokeServiceMethod() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.ADMIN, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder httpRequest = patch("/users/{userId}/status", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel"));
        verify(userService).changeStatus(any());
    }

    @Test
    void patchRequestToChangeUserStatus_fromNormalUser_shouldReturn404StatusCodeAndViewNotFound() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder httpRequest = patch("/users/{userId}/status", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(view().name("access-denied"));
        verifyNoInteractions(userService);
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
