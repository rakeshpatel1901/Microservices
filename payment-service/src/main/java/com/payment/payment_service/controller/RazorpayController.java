package com.payment.payment_service.controller;


import com.payment.payment_service.dto.FailureDto;
import com.payment.payment_service.dto.PaymentDTO;
import com.payment.payment_service.dto.PaymentVerifyDto;
import com.payment.payment_service.dto.RazorpayResponseDto;
import com.payment.payment_service.dto.types.PaymentStatus;
import com.payment.payment_service.entity.Payments;
import com.payment.payment_service.external.UserOrderClient;
import com.payment.payment_service.repository.PaymentRepository;
import com.payment.payment_service.service.RazorpayService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@Slf4j
public class RazorpayController {

    private final RazorpayService razorpayService;
    private final UserOrderClient userOrderClient;
    private final PaymentRepository paymentRepository;
    private final RazorpayClient razorpayClient;
    @Value("${razorpay.webhook}")
    private String webhookSecret;

    @Value("${razorpay.secret}")
    private String razorpaySecret;


    @Autowired
    public RazorpayController(RazorpayService razorpayService,
                              UserOrderClient userOrderClient,
                              PaymentRepository paymentRepository,
                              RazorpayClient razorpayClient){
        this.razorpayService = razorpayService;
        this.userOrderClient = userOrderClient;
        this.paymentRepository = paymentRepository;
        this.razorpayClient = razorpayClient;
    }

    @PostMapping("/create-online/{orderId}")
    public RazorpayResponseDto createOrder(@PathVariable Long orderId,
                                                           @RequestParam BigDecimal amount) throws Exception{
        return razorpayService.createPaymentOrder(orderId,amount);
    }

    @PostMapping("/create-offline/{orderId}")
    public boolean createOfflineOrder(@PathVariable Long orderId,
                                                  @RequestParam BigDecimal amount) throws Exception{
        return razorpayService.createPaymentOrderOffline(orderId,amount);
    }

    @Transactional
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payload) throws Exception {
        System.out.println("INSIDE THE WEBHOOK OF RAZORPAY");
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
            String paymentMode = razorpayClient.payments.fetch(paymentId).get("method");
            userOrderClient.paymentSuccessHandle(razorpayOrderId, payments.getId(),paymentMode);
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

    @Transactional
    @PostMapping("/failure")
    public ResponseEntity<String> handleFailure(@RequestBody FailureDto dto) {

        Payments payment = paymentRepository
                .findByRazorpayOrderId(dto.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        userOrderClient.paymentFailureHandle(dto.getRazorpayOrderId());

        return ResponseEntity.ok("Failure recorded");
    }

    @Transactional
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerifyDto dto) throws RazorpayException {
        String payload = dto.getRazorpayOrderId() + "|" + dto.getPaymentId();

        boolean isValid = Utils.verifySignature(
                payload,
                dto.getSignature(),
                razorpaySecret
        );
        String paymentMode = razorpayClient.payments.fetch(dto.getPaymentId()).get("method");

        if (!isValid) {
            return ResponseEntity.badRequest().body("Invalid payment");
        }

        Payments payment = paymentRepository
                .findByRazorpayOrderId(dto.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setPaymentId(dto.getPaymentId());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        userOrderClient.paymentSuccessHandle(dto.getRazorpayOrderId(), payment.getId(),paymentMode);

        return ResponseEntity.ok("Payment verified");
    }

    @GetMapping("/by-orders")
    List<PaymentDTO> getPaymentsByOrderIds(@RequestParam("orderIds") List<Long> orderIds){
        return razorpayService.getPaymentsByOrderIds(orderIds);
    }
}
