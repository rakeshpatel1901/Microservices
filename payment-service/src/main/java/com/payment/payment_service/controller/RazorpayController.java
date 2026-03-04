package com.payment.payment_service.controller;


import com.payment.payment_service.dto.RazorpayResponseDto;
import com.payment.payment_service.dto.types.PaymentStatus;
import com.payment.payment_service.entity.Payments;
import com.payment.payment_service.external.UserOrderClient;
import com.payment.payment_service.repository.PaymentRepository;
import com.payment.payment_service.service.RazorpayService;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payment")
@Slf4j
public class RazorpayController {

    private final RazorpayService razorpayService;
    private final UserOrderClient userOrderClient;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.webhook}")
    private String webhookSecret;


    @Autowired
    public RazorpayController(RazorpayService razorpayService,
                              UserOrderClient userOrderClient,
                              PaymentRepository paymentRepository){
        this.razorpayService = razorpayService;
        this.userOrderClient = userOrderClient;
        this.paymentRepository = paymentRepository;

    }

    @PostMapping("/create/{orderId}")
    public RazorpayResponseDto createOrder(@PathVariable Long orderId,
                                                           @RequestParam BigDecimal amount) throws Exception{
        return razorpayService.createPaymentOrder(orderId,amount);
    }

    @Transactional
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payload) throws Exception {
        log.info("Webhook Payload: {}", payload);
        boolean isValid = Utils.verifyWebhookSignature(
                payload,
                signature,
                webhookSecret
        );

        if (!isValid) {
            log.error("Invalid Razorpay Signature");
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        JSONObject paymentEntity =
                json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

        String razorpayOrderId = paymentEntity.getString("order_id");
        String paymentId = paymentEntity.getString("id");

        if ("payment.captured".equals(event)) {
            Payments payments = paymentRepository.findByRazorpayOrderId(razorpayOrderId).
                    orElseThrow(()-> new IllegalArgumentException("Payment Does not found"));

            payments.setStatus(PaymentStatus.SUCCESS);
            payments.setPaymentId(paymentId);
            paymentRepository.save(payments);
            userOrderClient.paymentSuccessHandle(razorpayOrderId, paymentId);
        }

        if ("payment.failed".equals(event)) {
            Payments payments = paymentRepository.findByRazorpayOrderId(razorpayOrderId).
                    orElseThrow(()-> new IllegalArgumentException("Payment Does not found"));

            payments.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payments);
            userOrderClient.paymentFailureHandle(razorpayOrderId);
        }

        return ResponseEntity.ok("Processed");
    }
}
