package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.pregnancy.service.PregnancyService;
import bg.softuni.myrabbitry.security.UserData;
import bg.softuni.myrabbitry.user.model.UserRole;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PregnancyController.class)
public class PregnancyControllerApiTest {

    @MockitoBean
    private PregnancyService pregnancyService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGetMaternityWardPage_shouldReturnViewMaternityWard_andStatusCodeOk_andModelPregnancyReports() throws Exception {

        when(pregnancyService.getAllUpComingPregnanciesForUser(any())).thenReturn(List.of());

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = get("/pregnancies")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("maternity-ward"))
                .andExpect(model().attributeExists("pregnancyReports"));
        verify(pregnancyService).getAllUpComingPregnanciesForUser(any());
    }

    @Test
    void whenGetPregnancyDetailsPage_shouldReturnViewPregnancyDetails_andStatusCodeOk_andModelPregnancyReportsAndPregnancyFilterRequest() throws Exception {

        when(pregnancyService.getAllPregnanciesForUser(any())).thenReturn(List.of());

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = get("/pregnancies/details")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("pregnancy-details"))
                .andExpect(model().attributeExists("pregnancyReports"))
                .andExpect(model().attributeExists("pregnancyFilterRequest"));
        verify(pregnancyService).getAllPregnanciesForUser(any());
    }

    @Test
    void whenGetSpecificPregnancyReport_shouldReturnViewPregnancyDetails_andStatusCodeOk_andModelPregnancyReportsAndPregnancyFilterRequest() throws Exception {

        when(pregnancyService.getById(any())).thenReturn(new PregnancyReport());

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = get("/pregnancies/details/" + UUID.randomUUID())
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("pregnancy-details"))
                .andExpect(model().attributeExists("pregnancyReports"))
                .andExpect(model().attributeExists("pregnancyFilterRequest"));
        verify(pregnancyService).getById(any());
    }

    @Test
    void whenGetEditPregnancyPage_shouldReturnViewEditPregnancy_andStatusCodeOk_andModelPregnancyRequestAndPregnancyReport() throws Exception {

        when(pregnancyService.getById(any())).thenReturn(new PregnancyReport());

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = get("/pregnancies/details/" + UUID.randomUUID() + "/report")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("edit-pregnancy"))
                .andExpect(model().attributeExists("pregnancyRequest"))
                .andExpect(model().attributeExists("pregnancyReport"));
        verify(pregnancyService).getById(any());
    }

    @Test
    void whenEditPregnancyReport_shouldReturnRedirectPregnanciesDetails_andStatusCode302() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = put("/pregnancies/details/" + UUID.randomUUID() + "/report")
                .with(user(authentication))
                .formField("mother", "12-qw")
                .formField("isFalsePregnancy", "false")
                .formField("isCannibalismPresent", "false")
                .formField("hasAbort", "false")
                .formField("wasPregnant", "true")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pregnancies/details"));
        verify(pregnancyService).editPregnancy(any(), any(), any());
    }

    @Test
    void whenEditPregnancyReport_andBindingResultHasErrors_shouldReturnViewEditPregnancy_andStatusCodeOk_andModelPregnancyReport() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = put("/pregnancies/details/" + UUID.randomUUID() + "/report")
                .with(user(authentication))
                .formField("mother", " ")
                .formField("isFalsePregnancy", "false")
                .formField("isCannibalismPresent", "false")
                .formField("hasAbort", "false")
                .formField("wasPregnant", "true")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pregnancies/details"));
        verify(pregnancyService).editPregnancy(any(), any(), any());
    }

    @Test
    void whenGetPregnancyReportsWithFilters_shouldReturnViewPregnancyDetails_andStatusCodeOk_andModelPregnancyReportsAndPregnancyFilterRequest() throws Exception {

        when(pregnancyService.getFilteredAndSortedPregnanciesForUser(any(), any())).thenReturn(List.of());

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = get("/pregnancies/details/group")
                .formField("mother", "12-qw")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("pregnancy-details"))
                .andExpect(model().attributeExists("pregnancyFilterRequest"))
                .andExpect(model().attributeExists("pregnancyReports"));
        verify(pregnancyService).getFilteredAndSortedPregnanciesForUser(any(), any());
    }

    @Test
    void whenGetAddPregnancyPage_shouldReturnViewAddPregnancy_andStatusCodeOk_andModelPregnancyRequest() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = get("/pregnancies/new")
                .with(user(authentication));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-pregnancy"))
                .andExpect(model().attributeExists("pregnancyRequest"));
    }

    @Test
    void whenCreatePregnancyReport_shouldReturnViewPregnancyDetails_andStatusCode302() throws Exception {

        UserData authentication = new UserData(UUID.randomUUID(), "Bolt", "123456", UserRole.USER, getLargeFarmPermissions(), true);
        MockHttpServletRequestBuilder request = post("/pregnancies/new")
                .with(user(authentication))
                .formField("mother", "12-qw")
                .formField("isFalsePregnancy", "false")
                .formField("isCannibalismPresent", "false")
                .formField("hasAbort", "false")
                .formField("wasPregnant", "true")
                .with(csrf());;

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pregnancies/details"));
        verify(pregnancyService).createPregnancyReport(any(), any());
    }

    private static List<String> getLargeFarmPermissions() {

        return List.of("view_pregnancy_details",
                "create_pregnancy_details",
                "edit_pregnancy_details",
                "view_my_rabbits",
                "create_rabbits",
                "edit_rabbits",
                "view_maternity_ward");
    }
}
