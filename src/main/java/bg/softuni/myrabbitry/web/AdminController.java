package bg.softuni.myrabbitry.web;

import bg.softuni.myrabbitry.payment.client.dto.ProfitReportResponse;
import bg.softuni.myrabbitry.payment.service.PaymentService;
import bg.softuni.myrabbitry.pregnancy.model.PregnancyReport;
import bg.softuni.myrabbitry.user.model.User;
import bg.softuni.myrabbitry.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    private final UserService userService;
    private final PaymentService paymentService;


    public AdminController(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping("/panel")
    public ModelAndView getAdminPanelPage() {

        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin-panel");
        modelAndView.addObject("users", users);

        return modelAndView;
    }

    @GetMapping("/reports/profit")
    public ModelAndView getProfitReportsPage() {

        List<ProfitReportResponse> oldProfitReports = paymentService.getOldProfitReports();
        ProfitReportResponse newProfitReport = paymentService.getLatestProfitReport();
        BigDecimal totalProfit = paymentService.getTotalProfit();
        long totalTransactions = paymentService.getTotalTransactions();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profit-reports");
        modelAndView.addObject("oldProfitReports", oldProfitReports);
        modelAndView.addObject("newProfitReport", newProfitReport);
        modelAndView.addObject("totalProfit", totalProfit);
        modelAndView.addObject("totalTransactions", totalTransactions);

        return modelAndView;
    }
}
