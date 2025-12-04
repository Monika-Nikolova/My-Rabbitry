package bg.softuni.myrabbitry.payment.service;

import bg.softuni.myrabbitry.exception.ReportStatusChangeFailedException;
import bg.softuni.myrabbitry.payment.client.PaymentClient;
import bg.softuni.myrabbitry.payment.client.dto.Payment;
import bg.softuni.myrabbitry.payment.client.dto.PaymentResponse;
import bg.softuni.myrabbitry.payment.client.dto.ProfitReportResponse;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.web.dto.PaymentRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentService {


    private final PaymentClient paymentClient;
    private final SubscriptionService subscriptionService;


    public PaymentService(PaymentClient paymentClient, SubscriptionService subscriptionService) {
        this.paymentClient = paymentClient;
        this.subscriptionService = subscriptionService;
    }

    public PaymentResponse makePayment(PaymentRequest paymentRequest, UUID id) {

        Payment payment = Payment.builder()
                .userId(id)
                .amount(subscriptionService.getSubscriptionPrice(paymentRequest.getSubscriptionType(), paymentRequest.getPeriod()))
                .sixteenDigitCode(paymentRequest.getSixteenDigitCode())
                .dateOfExpiry(paymentRequest.getExpiry())
                .cvvCode(paymentRequest.getCvv())
                .cardTier(paymentRequest.getCardTier())
                .subscriptionType(paymentRequest.getSubscriptionType().name())
                .period(paymentRequest.getPeriod().name())
                .build();

        PaymentResponse body;
        try {
            body = paymentClient.makePayment(payment).getBody();
        } catch (FeignException ex) {
            logError(ex);
            throw new RuntimeException("payment-svc is down");
        }

        log.info("Payment made for user [{}] for a {} {} subscription", id, paymentRequest.getPeriod(), paymentRequest.getSubscriptionType());

        return body;
    }

    public PaymentResponse getTransactionById(UUID id) {
        return paymentClient.getTransactionById(id).getBody();
    }

    public List<ProfitReportResponse> getOldProfitReports() {

        List<ProfitReportResponse> body = paymentClient.getOldProfileReports().getBody();

        if (body == null) {
            return new ArrayList<>();
        }

        return body.stream().filter(report -> report.getStatus().equals("Not Processed")).collect(Collectors.toList());
    }

    public ProfitReportResponse getLatestProfitReport() {
        return paymentClient.getLatestProfitReport().getBody();
    }

    public BigDecimal getTotalProfit() {

        List<ProfitReportResponse> profitReports = paymentClient.getAllProfitReports().getBody();

        if (profitReports == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalProfit = BigDecimal.ZERO;
        for (ProfitReportResponse profitReport : profitReports) {
            totalProfit = totalProfit.add(profitReport.getAmount());
        }

        return totalProfit;
    }

    public long getTotalTransactions() {

        List<ProfitReportResponse> profitReports = paymentClient.getAllProfitReports().getBody();

        if (profitReports == null) {
            return 0;
        }

        long totalTransactions = 0;
        for (ProfitReportResponse profitReport : profitReports) {
            totalTransactions += profitReport.getNumberOfTransactions();
        }

        return totalTransactions;
    }

    public void changeReportStatus(UUID id) {

        try {
            paymentClient.changeReportStatus(id);
        } catch (FeignException ex) {
            logError(ex);
            if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                throw new ReportStatusChangeFailedException("Failed to change report status.");
            } else {
                throw new RuntimeException("notification-svc is down");
            }
        }
    }

    private static void logError(FeignException ex) {
        log.error("[S2S Call]: Failed due to {}.", ex.getMessage());
    }
}
