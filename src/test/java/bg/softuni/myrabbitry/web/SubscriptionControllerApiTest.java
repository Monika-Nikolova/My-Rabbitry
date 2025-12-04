package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionStatus;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.model.UserRole;
import bg.softuni.myrabbitry.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
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

@WebMvcTest(SubscriptionController.class)
public class SubscriptionControllerApiTest {

    @MockitoBean
    private  UserService userService;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getSubscriptionPage_shouldReturnSubscriptionView_andStatusOk() throws Exception {

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

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = get("/subscriptions")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(view().name("subscription"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentSubscription"))
                .andExpect(model().attributeExists("subscriptionRequest"));
        verify(userService).getById(any());
    }

    @Test
    void getSubscriptionHistoryPage_shouldReturnSubscriptionHistoryView_andStatusOk() throws Exception {

        Subscription subscription = Subscription.builder()
                .isDeleted(false)
                .id(UUID.randomUUID())
                .status(SubscriptionStatus.ACTIVE)
                .period(SubscriptionPeriod.YEARLY)
                .type(SubscriptionType.LARGE_FARM)
                .price(BigDecimal.valueOf(55))
                .createdOn(LocalDateTime.now())
                .expirationOn(LocalDateTime.now().plusYears(1))
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

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = get("/subscriptions/history")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(view().name("subscription-history"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("subscriptions"));
        verify(userService).getById(any());
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
