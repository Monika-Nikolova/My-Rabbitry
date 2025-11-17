package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.web.dto.FamilyTreeDto;
import bg.softuni.myrabbitry.web.dto.FamilyTreeRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/family-tree")
@PreAuthorize("hasAuthority('view_family_tree')")
public class FamilyTreeController {


    private final RabbitService rabbitService;

    public FamilyTreeController(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @GetMapping
    public ModelAndView getFamilyTreePage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("family-tree");
        modelAndView.addObject("familyTreeRequest", new FamilyTreeRequest());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView makeFamilyTree(@Valid FamilyTreeRequest familyTreeRequest, RedirectAttributes redirectAttributes, BindingResult bindingResult, @AuthenticationPrincipal UserData  userData) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("family-tree");
        }

        FamilyTreeDto familyTree = rabbitService.createFamilyTree(familyTreeRequest.getCode(), userData.getId());
        redirectAttributes.addFlashAttribute("familyTree", familyTree);

        return new ModelAndView("redirect:/family-tree");
    }
}
