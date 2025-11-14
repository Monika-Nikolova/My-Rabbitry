package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{id}/status")
    public ModelAndView updateStatus(@PathVariable UUID id) {

        userService.changeStatus(id);

        return new ModelAndView("redirect:/admin/panel");
    }

    @PatchMapping("/{id}/role")
    public ModelAndView changeUserRole(@PathVariable UUID id) {

        userService.changeRole(id);

        return new ModelAndView("redirect:/admin/panel");
    }
}
