package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.payment.client.dto.PaymentResponse;
import bg.softuni.myrabbitry.payment.service.PaymentService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.subscription.model.CardTier;
import bg.softuni.myrabbitry.subscription.model.Subscription;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGetPaymentFormPage_shouldReturnPaymentFormView_andStatusOk_andModelPaymentRequest() throws Exception {

        when(userService.getById(any())).thenReturn(new User());

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = get("/payments/new?subscriptionType=LARGE_FARM")
                .with(user(authentication))
                .formField("period", "YEARLY")
                .formField("cardTier", "VISA")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("payment-form"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("paymentRequest"));
    }

    @Test
    void whenGetPaymentFormPage_andBindingResultHasErrors_shouldReturnSubscriptionView_andStatusOk() throws Exception {

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
        MockHttpServletRequestBuilder request = get("/payments/new?subscriptionType=LARGE_FARM")
                .with(user(authentication))
                .formField("period", "")
                .formField("cardTier", "VISA")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("subscription"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentSubscription"));
    }

    @Test
    void whenMakePayment_andBindingResultHasErrors_shouldReturnSubscriptionView_andStatusOk() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = post("/payments/new")
                .with(user(authentication))
                .formField("subscriptionType", "LARGE_FARM")
                .formField("sixteenDigitCode", "1234567890")
                .formField("cvv", "123")
                .formField("period", "")
                .formField("cardTier", "VISA")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("payment-form"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("paymentRequest"));
    }

    @Test
    void whenMakePayment_shouldReturnRedirect302_andInvokeMakePayment() throws Exception {

        UUID transactionId = UUID.randomUUID();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .status("NOT_PROCESSED")
                .amount(BigDecimal.valueOf(55))
                .cardTier(CardTier.VISA)
                .lastFourDigitsOfCardNumber(3456)
                .transactionId(transactionId)
                .period(SubscriptionPeriod.YEARLY)
                .subscriptionType(SubscriptionType.LARGE_FARM)
                .build();

        when(paymentService.makePayment(any(), any())).thenReturn(paymentResponse);

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = post("/payments/new")
                .with(user(authentication))
                .formField("subscriptionType", "LARGE_FARM")
                .formField("sixteenDigitCode", "1234567890123456")
                .formField("expiry", "2027-12")
                .formField("cvv", "123")
                .formField("cardTier", "VISA")
                .formField("period", "YEARLY")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(redirectedUrl("/payments/transactions/" + transactionId + "?subscriptionType=LARGE_FARM&period=YEARLY"))
                .andExpect(status().is3xxRedirection());
        verify(paymentService).makePayment(any(), any());
    }

    @Test
    void getPaymentAnswerPage_shouldReturnViewPaymentAnswer_andStatusOk_andModels() throws Exception {

        UUID transactionId = UUID.randomUUID();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .status("NOT_PROCESSED")
                .amount(BigDecimal.valueOf(55))
                .cardTier(CardTier.VISA)
                .lastFourDigitsOfCardNumber(3456)
                .transactionId(transactionId)
                .period(SubscriptionPeriod.YEARLY)
                .subscriptionType(SubscriptionType.LARGE_FARM)
                .build();

        when(paymentService.getTransactionById(any())).thenReturn(paymentResponse);

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = get("/payments/transactions/" + UUID.randomUUID() + "?subscriptionType=LARGE_FARM&period=YEARLY")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(view().name("payment-answer"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("transaction"))
                .andExpect(model().attributeExists("subscriptionType"))
                .andExpect(model().attributeExists("period"));
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
