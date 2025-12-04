package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.rabbit.service.RabbitService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.user.model.UserRole;
import bg.softuni.myrabbitry.web.dto.FamilyTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FamilyTreeController.class)
public class FamilyTreeControllerApiTest {

    @MockitoBean
    private RabbitService rabbitService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFamilyTreePage_shouldReturnStatusOk_andViewFamilyTree() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = get("/family-tree")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(view().name("family-tree"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("familyTreeRequest"));
    }

    @Test
    void makeFamilyTree_shouldReturnStatus302_andRedirect() throws Exception {

        when(rabbitService.createFamilyTree(any(), any())).thenReturn(new FamilyTreeDto());

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getDefaultPermissions(), true);
        MockHttpServletRequestBuilder request = post("/family-tree")
                .with(user(authentication))
                .formField("code", "12-qw")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(redirectedUrl("/family-tree"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("familyTree"));
        verify(rabbitService).createFamilyTree(any(), any());
    }

    private static List<String> getDefaultPermissions() {

        return List.of("view_pregnancy_details",
                "create_pregnancy_details",
                "edit_pregnancy_details",
                "view_my_rabbits",
                "create_rabbits",
                "edit_rabbits",
                "view_family_tree");
    }
}
