package bg.softuni.myrabbitry.payment.service;

import bg.softuni.myrabbitry.payment.client.PaymentClient;
import bg.softuni.myrabbitry.payment.client.dto.Payment;
import bg.softuni.myrabbitry.payment.client.dto.PaymentResponse;
import bg.softuni.myrabbitry.payment.client.dto.ProfitReportResponse;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.web.dto.PaymentRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

        return paymentClient.makePayment(payment).getBody();
    }

    public PaymentResponse getTransactionById(UUID id) {
        return paymentClient.getTransactionById(id).getBody();
    }

    public List<ProfitReportResponse> getOldProfitReports() {
        return paymentClient.getOldProfileReports().getBody();
    }

    public ProfitReportResponse getLatestProfitReport() {
        return paymentClient.getLatestProfitReport().getBody();
    }

    public BigDecimal getTotalProfit(List<ProfitReportResponse> oldProfitReports, BigDecimal newProfit) {
        BigDecimal totalProfit = BigDecimal.ZERO;
        for (ProfitReportResponse profitReport : oldProfitReports) {
            totalProfit = totalProfit.add(profitReport.getAmount());
        }
        return totalProfit.add(newProfit);
    }

    public long getTotalTransactions(List<ProfitReportResponse> oldProfitReports, long numberOfTransactions) {
        long totalTransactions = 0;
        for (ProfitReportResponse profitReport : oldProfitReports) {
            totalTransactions += profitReport.getNumberOfTransactions();
        }
        return totalTransactions + numberOfTransactions;
    }
}
