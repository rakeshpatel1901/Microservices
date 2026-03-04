package com.learning.orderservice.controller;


import com.learning.orderservice.dto.ApiResponseDto;
import com.learning.orderservice.dto.OrderResponseDto;
import com.learning.orderservice.dto.ProductVariantRequestDto;
import com.learning.orderservice.dto.types.OrderStatus;
import com.learning.orderservice.entity.Orders;
import com.learning.orderservice.externalservices.PaymentClient;
import com.learning.orderservice.externalservices.ProductClient;
import com.learning.orderservice.repository.OrderRepository;
import com.learning.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;

    private final ProductClient productClient;

    private final OrderService orderService;

    public OrderController(ProductClient productClient,
                           OrderService orderService,
                           OrderRepository orderRepository){
        this.orderService = orderService;
        this.productClient = productClient;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/place-order/{productVariantId}/{quantity}")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> placeOrder(@PathVariable Long productVariantId,
                                                             @PathVariable Integer quantity) throws Exception {
        System.out.println("Hitting by razorpay html");
        ApiResponseDto<ProductVariantRequestDto> response = productClient.reserveStockProductVariantById(productVariantId, quantity);
        OrderResponseDto orderResponseDto = null ;
        if(response.getStatus().equalsIgnoreCase("SUCCESS")){
            orderResponseDto = orderService.createOrder(quantity, response);
        }
        ApiResponseDto<OrderResponseDto> orderResponse = new ApiResponseDto<>();
        orderResponse.setMessage("Order Details ");
        orderResponse.setData(orderResponseDto);


        return ResponseEntity.ok(orderResponse);

    }

    @PostMapping("/payment-success/{razorpayOrderId}/{paymentId}")
    public ResponseEntity<ApiResponseDto<String>> paymentSuccessHandle(
            @PathVariable String razorpayOrderId,
            @PathVariable String paymentId){
        log.info("Inside the Payment Success");
        Orders orders = orderRepository.findByRazorpayOrderId(razorpayOrderId).
                orElseThrow(() -> new IllegalArgumentException("Razorpay order id not found"));

        orders.setStatus(OrderStatus.PAYMENT_SUCCESS);
        orders.setPaymentId(paymentId);

        orderRepository.save(orders);
        ApiResponseDto<String> apiResponseDto = new ApiResponseDto<>();
        apiResponseDto.setMessage("Order has been Placed Successfully");
        apiResponseDto.setData("Order has been Placed Successfully");
        apiResponseDto.setStatus("SUCCESS");
        return ResponseEntity.ok(apiResponseDto);
    }

    @PostMapping("/payment-failure/{razorpayOrderId}")
    public ResponseEntity<ApiResponseDto<String>> paymentFailureHandle(
            @PathVariable String razorpayOrderId){
        log.info("Inside the Payment Failure");
        Orders orders = orderRepository.findByRazorpayOrderId(razorpayOrderId).
                orElseThrow(() -> new IllegalArgumentException("Razorpay order id not found"));

        orders.setStatus(OrderStatus.PAYMENT_FAILED);

        productClient.unreserveStockProductVariantById(orders.getProductVariantId(), orders.getQuantity());

        orderRepository.save(orders);
        ApiResponseDto<String> apiResponseDto = new ApiResponseDto<>();
        apiResponseDto.setMessage("Failed to make Payment");
        apiResponseDto.setData("Failed to make Payment");
        apiResponseDto.setStatus("FAILED");
        return ResponseEntity.ok(apiResponseDto);
    }



}
