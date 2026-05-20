package com.payment.payment_service.service.implementation;

import com.payment.payment_service.dto.PaymentDTO;
import com.payment.payment_service.dto.RazorpayResponseDto;
import com.payment.payment_service.dto.types.PaymentStatus;
import com.payment.payment_service.entity.Payments;
import com.payment.payment_service.repository.PaymentRepository;
import com.payment.payment_service.service.RazorpayService;
import com.razorpay.Order;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@Slf4j
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    @Autowired
    public RazorpayServiceImpl(RazorpayClient razorpayClient, PaymentRepository paymentRepository){
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    @Override
    public RazorpayResponseDto createPaymentOrder(Long orderId, BigDecimal amount) throws RazorpayException {

        JSONObject options = new JSONObject();
        options.put("amount", amount.multiply(BigDecimal.valueOf(100)));
        options.put("currency", "INR");
        options.put("receipt","order_"+orderId);


        Order razorpay = razorpayClient.orders.create(options);

        Payments payment = new Payments();
        payment.setOrderId(orderId);
        payment.setRazorpayOrderId(razorpay.get("id"));
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.CREATED);

        paymentRepository.save(payment);
        Number amountNumber = (Number) razorpay.get("amount");

        return new RazorpayResponseDto(
                razorpay.get("id"),
                amountNumber.longValue(),
                razorpay.get("currency")
        );

    }

    @Transactional
    @Override
    public boolean createPaymentOrderOffline(Long orderId, BigDecimal amount) throws RazorpayException {
        try{

        Payments payment = new Payments();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.COD_PENDING);
        paymentRepository.save(payment);
        return true;
        }
        catch (Exception e){
            throw e;
        }

    }

    @Override
    public List<PaymentDTO> getPaymentsByOrderIds(List<Long> orderIds) {

        List<Payments> payments =
                paymentRepository.findByOrderIdIn(orderIds);

        return payments.stream()
                .map(payment -> {
                    PaymentDTO dto = new PaymentDTO();

                    dto.setOrderId(payment.getOrderId());
                    dto.setPaymentId(payment.getPaymentId());
                    dto.setStatus(payment.getStatus().name());

                    return dto;
                })
                .toList();
    }
}
