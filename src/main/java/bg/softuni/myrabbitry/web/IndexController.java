package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.web.dto.LoginRequest;
import bg.softuni.myrabbitry.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class IndexController {


    private final UserService userService;
    private final PregnancyService pregnancyService;

    public IndexController(UserService userService, PregnancyService pregnancyService) {
        this.userService = userService;
        this.pregnancyService = pregnancyService;
    }

    @GetMapping
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerRequest);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/dashboard")
    public ModelAndView getDashboardPage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getId());
        PregnancyReport pregnancyReport = pregnancyService.getLatest(user.getRabbits());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("dashboard");
        modelAndView.addObject("user", user);
        modelAndView.addObject("latestRabbit", user.getRabbits().stream().findFirst().orElse(null));
        modelAndView.addObject("pregnancyReport", pregnancyReport);

        return modelAndView;
    }
}
