package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import bg.softuni.myrabbitry.utils.RabbitryUtils;
import bg.softuni.myrabbitry.web.dto.BestParent;
import bg.softuni.myrabbitry.web.dto.RabbitryStats;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/overview")
@PreAuthorize("hasAuthority('view_overview')")
public class OverviewController {


    private final UserService userService;
    private final PregnancyService pregnancyService;

    public OverviewController(UserService userService, PregnancyService pregnancyService) {
        this.userService = userService;
        this.pregnancyService = pregnancyService;
    }

    @GetMapping
    public ModelAndView getOverviewPage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getId());
        RabbitryStats stats = RabbitryUtils.getRabbitryStats(user.getRabbits());
        BestParent bestMother = pregnancyService.getBestMother(userData.getId());
        BestParent bestFather = pregnancyService.getBestFather(userData.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("overview");
        modelAndView.addObject("stats", stats);
        modelAndView.addObject("bestMother", bestMother);
        modelAndView.addObject("bestFather", bestFather);
        modelAndView.addObject("processed", RabbitryUtils.getStatusPercentage(user.getRabbits(), "PROCESSED"));
        modelAndView.addObject("sold", RabbitryUtils.getStatusPercentage(user.getRabbits(), "SOLD"));
        modelAndView.addObject("forMeat", RabbitryUtils.getStatusPercentage(user.getRabbits(), "FOR_MEAT"));
        modelAndView.addObject("inactive", RabbitryUtils.getStatusPercentage(user.getRabbits(), "INACTIVE"));
        modelAndView.addObject("forBreeding", RabbitryUtils.getStatusPercentage(user.getRabbits(), "FOR_BREEDING"));

        return modelAndView;
    }
}
