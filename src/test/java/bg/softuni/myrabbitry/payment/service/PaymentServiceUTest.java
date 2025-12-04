package bg.softuni.myrabbitry.payment.service;

import bg.softuni.myrabbitry.exception.ReportStatusChangeFailedException;
import bg.softuni.myrabbitry.payment.client.PaymentClient;
import bg.softuni.myrabbitry.payment.client.dto.PaymentResponse;
import bg.softuni.myrabbitry.payment.client.dto.ProfitReportResponse;
import bg.softuni.myrabbitry.subscription.model.CardTier;
import bg.softuni.myrabbitry.subscription.model.SubscriptionPeriod;
import bg.softuni.myrabbitry.subscription.model.SubscriptionType;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.web.dto.PaymentRequest;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUTest {

    @Mock
    private PaymentClient paymentClient;
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void whenMakePayment_andPaymentClientDoesNotThrowException_thenReturnPaymentResponse() {

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .subscriptionType(SubscriptionType.LARGE_FARM)
                .sixteenDigitCode("1234567890123456")
                .expiry(YearMonth.of(2027, 7))
                .cvv("123")
                .cardTier(CardTier.VISA)
                .period(SubscriptionPeriod.YEARLY)
                .build();
        UUID id = UUID.randomUUID();

        PaymentResponse paymentResponse = PaymentResponse.builder().build();
        ResponseEntity<PaymentResponse> response = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentResponse);
        when(paymentClient.makePayment(any())).thenReturn(response);

        paymentService.makePayment(paymentRequest, id);

        verify(paymentClient).makePayment(any());
    }

    @Test
    void whenMakePayment_andPaymentClientThrowsException_thenThrowRuntimeException() {

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .subscriptionType(SubscriptionType.LARGE_FARM)
                .sixteenDigitCode("1234567890123456")
                .expiry(YearMonth.of(2027, 7))
                .cvv("123")
                .cardTier(CardTier.VISA)
                .period(SubscriptionPeriod.YEARLY)
                .build();
        UUID id = UUID.randomUUID();
        when(paymentClient.makePayment(any())).thenThrow(FeignException.class);

        assertThrows(RuntimeException.class, () ->paymentService.makePayment(paymentRequest, id));
        verify(paymentClient).makePayment(any());
    }

    @Test
    void whenGetOldProfitReports_thenReturnOnlyNotProcessedReports() {

        ProfitReportResponse response = ProfitReportResponse.builder()
                .status("Processed")
                .build();
        ProfitReportResponse response2 = ProfitReportResponse.builder()
                .status("Not Processed")
                .build();
        ProfitReportResponse response3 = ProfitReportResponse.builder()
                .status("Not Processed")
                .build();

        ResponseEntity<List<ProfitReportResponse>> oldProfitReports = ResponseEntity.ok(List.of(response, response2, response3));
        when(paymentClient.getOldProfileReports()).thenReturn(oldProfitReports);

        List<ProfitReportResponse> body = paymentService.getOldProfitReports();

        assertThat(body).hasSize(2);
        assertEquals("Not Processed", body.get(0).getStatus());
        assertEquals("Not Processed", body.get(1).getStatus());
    }

    @Test
    void whenGetOldProfitReports_andThereAreNoProfitReports_thenReturnEmptyList() {

        when(paymentClient.getOldProfileReports()).thenReturn(ResponseEntity.noContent().build());

        List<ProfitReportResponse> body = paymentService.getOldProfitReports();

        assertThat(body).hasSize(0);
    }

    @Test
    void whenGetTotalProfit_andThereAre3ProfitReportsWith55Amount_thenTotalProfit165() {

        ProfitReportResponse response = ProfitReportResponse.builder()
                .status("Processed")
                .amount(BigDecimal.valueOf(55))
                .build();
        ProfitReportResponse response2 = ProfitReportResponse.builder()
                .status("Not Processed")
                .amount(BigDecimal.valueOf(55))
                .build();
        ProfitReportResponse response3 = ProfitReportResponse.builder()
                .status("Not Processed")
                .amount(BigDecimal.valueOf(55))
                .build();

        ResponseEntity<List<ProfitReportResponse>> oldProfitReports = ResponseEntity.ok(List.of(response, response2, response3));
        when(paymentClient.getAllProfitReports()).thenReturn(oldProfitReports);

        BigDecimal totalProfit = paymentService.getTotalProfit();

        assertEquals(BigDecimal.valueOf(165), totalProfit);
    }

    @Test
    void whenGetTotalProfit_andProfitReportsAreNull_thenReturnZero() {

        ResponseEntity<List<ProfitReportResponse>> oldProfitReports = ResponseEntity.ok(null);
        when(paymentClient.getAllProfitReports()).thenReturn(oldProfitReports);

        BigDecimal totalProfit = paymentService.getTotalProfit();

        assertEquals(BigDecimal.ZERO, totalProfit);
    }

    @Test
    void whenGetTotalTransactions_andThereAre3ProfitReportsWith4Transactions_thenTotalNumberOfTransactionsIs12() {

        ProfitReportResponse response = ProfitReportResponse.builder()
                .status("Processed")
                .numberOfTransactions(4)
                .build();
        ProfitReportResponse response2 = ProfitReportResponse.builder()
                .status("Not Processed")
                .numberOfTransactions(4)
                .build();
        ProfitReportResponse response3 = ProfitReportResponse.builder()
                .status("Not Processed")
                .numberOfTransactions(4)
                .build();

        ResponseEntity<List<ProfitReportResponse>> oldProfitReports = ResponseEntity.ok(List.of(response, response2, response3));
        when(paymentClient.getAllProfitReports()).thenReturn(oldProfitReports);

        long totalTransactions = paymentService.getTotalTransactions();

        assertEquals(12, totalTransactions);
    }

    @Test
    void whenGetTotalTransactions_andProfitReportsAreNull_thenTotalNumberOfTransactionsIs0() {

        ResponseEntity<List<ProfitReportResponse>> oldProfitReports = ResponseEntity.ok(null);
        when(paymentClient.getAllProfitReports()).thenReturn(oldProfitReports);

        long totalTransactions = paymentService.getTotalTransactions();

        assertEquals(0, totalTransactions);
    }

    @Test
    void whenChangeReportStatus_andNoExceptionIsThrownFromClient() {

        when(paymentClient.changeReportStatus(any())).thenReturn(null);

        paymentService.changeReportStatus(UUID.randomUUID());

        verify(paymentClient).changeReportStatus(any());
    }

    @Test
    void whenChangeReportStatus_andFeignExceptionIsThrownFromClientWithStatusCodeNotFound_thenThrowReportStatusChangeFailedException() {

        Request request = Request.create(
                Request.HttpMethod.PUT,
                "/reports/profit/{id}/status",
                Map.of(),
                null,
                UTF_8,
                null
        );

        Response response = Response.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .reason("Not Found")
                .request(request)
                .headers(Map.of())
                .build();

        FeignException feignException = FeignException.errorStatus("getSomething", response);
        when(paymentClient.changeReportStatus(any())).thenThrow(feignException);

        assertThrows(ReportStatusChangeFailedException.class, () -> paymentService.changeReportStatus(UUID.randomUUID()));
        verify(paymentClient).changeReportStatus(any());
    }

    @Test
    void whenChangeReportStatus_andFeignExceptionIsThrownFromClientWithStatusCodeOtherThanNotFound_thenThrowRuntimeException() {

        when(paymentClient.changeReportStatus(any())).thenThrow(FeignException.class);

        assertThrows(RuntimeException.class, () -> paymentService.changeReportStatus(UUID.randomUUID()));
        verify(paymentClient).changeReportStatus(any());
    }
}
