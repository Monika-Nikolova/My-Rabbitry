package bg.softuni.myrabbitry.payment.client;

import bg.softuni.myrabbitry.payment.client.dto.Payment;
import bg.softuni.myrabbitry.payment.client.dto.PaymentResponse;
import bg.softuni.myrabbitry.payment.client.dto.ProfitReportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "payment-svc", url = "http://localhost:8081/api/v1")
public interface PaymentClient {

    @PostMapping("/transactions")
    ResponseEntity<PaymentResponse> makePayment(@RequestBody Payment requestBody);

    @GetMapping("/transactions")
    ResponseEntity<PaymentResponse> getTransactionById(@RequestParam("transactionId") UUID transactionId);

    @GetMapping("/reports/profit/latest")
    ResponseEntity<ProfitReportResponse> getLatestProfitReport();

    @GetMapping("/reports/profit/old")
    ResponseEntity<List<ProfitReportResponse>> getOldProfileReports();
}
