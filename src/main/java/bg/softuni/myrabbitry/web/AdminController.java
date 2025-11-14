package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/panel")
    public ModelAndView getAdminPanelPage() {

        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin-panel");
        modelAndView.addObject("users", users);

        return modelAndView;
    }
}
