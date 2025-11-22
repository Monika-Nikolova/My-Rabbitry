package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.payment.client.dto.PaymentResponse;
import bg.softuni.myrabbitry.payment.service.PaymentService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.DtoMapper;
import bg.softuni.myrabbitry.web.dto.PaymentRequest;
import bg.softuni.myrabbitry.web.dto.SubscriptionRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/payments")
public class PaymentController {


    private final UserService userService;
    private final PaymentService paymentService;

    public PaymentController(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping("/new")
    public ModelAndView getPaymentFormPage(@AuthenticationPrincipal UserData userData, @Valid SubscriptionRequest subscriptionRequest, BindingResult bindingResult, @RequestParam("subscriptionType") SubscriptionType subscriptionType) {

        User user = userService.getById(userData.getId());

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("subscription");
            modelAndView.addObject("currentSubscription", user.getSubscriptions().get(0));
            return modelAndView;
        }

        modelAndView.setViewName("payment-form");
        modelAndView.addObject("paymentRequest", DtoMapper.fromSubscriptionRequestToPaymentRequest(subscriptionType, subscriptionRequest));

        return modelAndView;
    }

    @PostMapping("/new")
    public ModelAndView makePayment(@Valid PaymentRequest paymentRequest, BindingResult bindingResult, @AuthenticationPrincipal UserData userData) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("payment-form");
            modelAndView.addObject(paymentRequest);
            return modelAndView;
        }

        PaymentResponse paymentResponse = paymentService.makePayment(paymentRequest, userData.getId());

        String redirectUrl = String.format("redirect:/payments/transactions/%s?subscriptionType=%s&period=%s", paymentResponse.getTransactionId(), paymentResponse.getSubscriptionType(), paymentResponse.getPeriod());

        return new  ModelAndView(redirectUrl);
    }

    @GetMapping("/transactions/{id}")
    public ModelAndView getSuccessPage(@PathVariable UUID id, @RequestParam("subscriptionType") SubscriptionType subscriptionType, @RequestParam("period")SubscriptionPeriod period) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("payment-answer");
        modelAndView.addObject("transaction", paymentService.getTransactionById(id));
        modelAndView.addObject("subscriptionType", subscriptionType);
        modelAndView.addObject("period", period);

        return modelAndView;
    }
}
