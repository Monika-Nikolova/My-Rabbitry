package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.SubscriptionRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {


    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public SubscriptionController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ModelAndView getSubscriptionPage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("subscription");
        modelAndView.addObject("currentSubscription", user.getSubscriptions().get(0));
        modelAndView.addObject("subscriptionRequest", new SubscriptionRequest());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView changeSubscription(@AuthenticationPrincipal UserData userData, @Valid SubscriptionRequest subscriptionRequest, BindingResult bindingResult, @RequestParam("subscriptionType") SubscriptionType subscriptionType) {

        User user = userService.getById(userData.getId());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("subscription");
            modelAndView.addObject("currentSubscription", user.getSubscriptions().get(0));
            return modelAndView;
        }

        subscriptionService.createNewSubscription(user, subscriptionRequest, subscriptionType);

        return new ModelAndView("redirect:/subscriptions");
    }
}
