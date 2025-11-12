package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.web.dto.PregnancyFilterRequest;
import bg.softuni.myrabbitry.web.dto.PregnancyRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/pregnancies")
public class PregnancyController {


    private final PregnancyService pregnancyService;

    public PregnancyController(PregnancyService pregnancyService) {
        this.pregnancyService = pregnancyService;
    }

    @GetMapping
    public ModelAndView getMaternityWardPage(@AuthenticationPrincipal UserData userData) {

        List<PregnancyReport> pregnancyReports = pregnancyService.getAllUpComingPregnanciesForUser(userData.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("maternity-ward");
        modelAndView.addObject("pregnancyReports", pregnancyReports);

        return modelAndView;
    }

    @GetMapping("/details")
    public ModelAndView getPregnancyDetailsPage(@AuthenticationPrincipal UserData userData) {

        List<PregnancyReport> pregnancyReports = pregnancyService.getAllPregnanciesForUser(userData.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pregnancy-details");
        modelAndView.addObject("pregnancyReports", pregnancyReports);
        modelAndView.addObject("pregnancyFilterRequest", new PregnancyFilterRequest());

        return modelAndView;
    }

    @GetMapping("/details/{id}")
    public ModelAndView getSpecificPregnancyReport(@PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pregnancy-details");
        modelAndView.addObject("pregnancyReports", List.of(pregnancyService.getById(id)));
        modelAndView.addObject("pregnancyFilterRequest", new PregnancyFilterRequest());

        return modelAndView;
    }

    @GetMapping("/details/group")
    public ModelAndView getPregnancyReportsWithFilter(@AuthenticationPrincipal UserData userData, PregnancyFilterRequest pregnancyFilterRequest) {

        List<PregnancyReport> pregnancyReports = pregnancyService.getFilteredAndSortedPregnanciesForUser(userData.getId(), pregnancyFilterRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pregnancy-details");
        modelAndView.addObject("pregnancyReports", pregnancyReports);
        modelAndView.addObject("pregnancyFilterRequest", new PregnancyFilterRequest());

        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView getAddPregnancyPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-pregnancy");
        modelAndView.addObject("pregnancyRequest", new PregnancyRequest());

        return modelAndView;
    }

    @PostMapping("/new")
    public ModelAndView createPregnancyReport(@Valid PregnancyRequest pregnancyRequest, @AuthenticationPrincipal UserData userData, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("add-pregnancy");
        }

        pregnancyService.createPregnancyReport(pregnancyRequest, userData.getId());

        return new ModelAndView("redirect:/pregnancies/details");
    }
}
