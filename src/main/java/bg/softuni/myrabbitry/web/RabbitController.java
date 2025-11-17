package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.rabbit.model.Rabbit;
import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.web.dto.DtoMapper;
import bg.softuni.myrabbitry.web.dto.RabbitRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/rabbits")
public class RabbitController {


    private final RabbitService rabbitService;

    public RabbitController(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PreAuthorize("hasAuthority('view_my_rabbits')")
    @GetMapping("/me")
    public ModelAndView getMyRabbitsPage(@AuthenticationPrincipal UserData userData) {

        List<Rabbit> rabbits = rabbitService.getByOwnerId(userData.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("my-rabbits");
        modelAndView.addObject("rabbits", rabbits);

        return modelAndView;
    }

    @PreAuthorize("hasAuthority('create_rabbits')")
    @GetMapping("/new")
    public ModelAndView getAddRabbitPage(@AuthenticationPrincipal UserData userData) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-rabbit");
        modelAndView.addObject("rabbitRequest", new RabbitRequest());

        return modelAndView;
    }

    @PreAuthorize("hasAuthority('create_rabbits')")
    @PostMapping("/new")
    public ModelAndView createNewRabbit(@AuthenticationPrincipal UserData userData, @Valid RabbitRequest rabbitRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-rabbit");
        }

        rabbitService.createNewRabbit(rabbitRequest, userData.getId());

        return new ModelAndView("redirect:/rabbits/me");
    }

    @PreAuthorize("hasAuthority('edit_rabbits')")
    @GetMapping("/me/{id}")
    public ModelAndView getEditRabbitPage(@PathVariable UUID id) {

        Rabbit rabbit = rabbitService.getById(id);
        RabbitRequest rabbitRequest = DtoMapper.fromRabbit(rabbit);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-rabbit");
        modelAndView.addObject("rabbitRequest", rabbitRequest);
        modelAndView.addObject("rabbit", rabbit);

        return modelAndView;
    }

    @PreAuthorize("hasAuthority('edit_rabbits')")
    @PutMapping("/me/{id}")
    public ModelAndView editRabbit(@PathVariable UUID id, @Valid RabbitRequest rabbitRequest, BindingResult bindingResult, @AuthenticationPrincipal UserData userData) {

        if (bindingResult.hasErrors()) {
            Rabbit rabbit = rabbitService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-rabbit");
            modelAndView.addObject("rabbit", rabbit);
        }

        rabbitService.editRabbit(id, rabbitRequest, userData.getId());

        return new ModelAndView("redirect:/rabbits/me");
    }
}
