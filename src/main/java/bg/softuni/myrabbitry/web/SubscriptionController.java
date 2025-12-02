package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.utils.SubscriptionUtils;
import bg.softuni.myrabbitry.web.dto.SubscriptionRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

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
    public ModelAndView changeSubscription(@AuthenticationPrincipal UserData userData, @RequestParam("period") SubscriptionPeriod period, @RequestParam("subscriptionType") SubscriptionType subscriptionType) {

        User user = userService.getById(userData.getId());

        subscriptionService.createNewSubscription(user, period, subscriptionType);

        return new ModelAndView("redirect:/subscriptions");
    }

    @GetMapping("/history")
    public ModelAndView getSubscriptionHistoryPage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("subscription-history");
        modelAndView.addObject("subscriptions", SubscriptionUtils.getNotDeletedSubscriptions(user.getSubscriptions()));

        return modelAndView;
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteSubscription(@PathVariable UUID id) {

        subscriptionService.deleteSubscription(id);

        return new ModelAndView("redirect:/subscriptions/history");
    }
}
