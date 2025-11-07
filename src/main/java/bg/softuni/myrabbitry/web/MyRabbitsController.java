package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.web.dto.RabbitRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/my-rabbits")
public class MyRabbitsController {


    private final RabbitService rabbitService;

    public MyRabbitsController(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @GetMapping
    public ModelAndView getMyRabbitsPage(@AuthenticationPrincipal UserData userData) {

        List<Rabbit> rabbits = rabbitService.getByOwnerId(userData.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("my-rabbits");
        modelAndView.addObject("rabbits", rabbits);

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getAddRabbitPage(@AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-rabbit");
        modelAndView.addObject("rabbitRequest", new RabbitRequest());

        return modelAndView;
    }

    @PostMapping("/new")
    public ModelAndView createNewRabbit(@AuthenticationPrincipal UserData userData, @Valid RabbitRequest rabbitRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-rabbit");
        }

        rabbitService.createNewRabbit(rabbitRequest, userData.getId());

        return new ModelAndView("redirect:/my-rabbits");
    }
}
