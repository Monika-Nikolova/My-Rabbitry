package bg.softuni.myrabbitry.payment.service;

import bg.softuni.myrabbitry.payment.client.PaymentClient;
import bg.softuni.myrabbitry.payment.client.dto.Payment;
import bg.softuni.myrabbitry.payment.client.dto.PaymentResponse;
import bg.softuni.myrabbitry.subscription.service.SubscriptionService;
import bg.softuni.myrabbitry.web.dto.PaymentRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
