package com.learning.orderservice.controller;


import com.learning.orderservice.dto.ApiResponseDto;
import com.learning.orderservice.dto.OrderRequestDto;
import com.learning.orderservice.dto.OrderResponseDto;
import com.learning.orderservice.dto.UserOrderResponseDto;
import com.learning.orderservice.dto.types.OrderStatus;
import com.learning.orderservice.entity.OrderItem;
import com.learning.orderservice.entity.Orders;
import com.learning.orderservice.externalservices.ProductClient;
import com.learning.orderservice.repository.OrderRepository;
import com.learning.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;

    private final ProductClient productClient;

    private final OrderService orderService;

    @Autowired
    StreamBridge streamBridge;

    public OrderController(ProductClient productClient,
                           OrderService orderService,
                           OrderRepository orderRepository) {
        this.orderService = orderService;
        this.productClient = productClient;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/checking")
    public String checkNoti() {
        System.out.println("Inside Order check Notif");
        //streamBridge.send("orderPlacedNotification-out-0", "This is send from order");
        return "Working";
    }

    @PostMapping("/place-order")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> placeOrder(
            @RequestBody OrderRequestDto request) throws Exception {
        System.out.println("Hitting by razorpay html");
        OrderResponseDto orderResponseDto = orderService.createOrder(request);

        streamBridge.send("orderPlacedNotification-out-0", "Order Placed");

        ApiResponseDto<OrderResponseDto> response = new ApiResponseDto<>();
        response.setMessage("Order Created");
        response.setData(orderResponseDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment-success/{razorpayOrderId}/{paymentId}/{paymentMode}")
    public ResponseEntity<ApiResponseDto<String>> paymentSuccessHandle(
            @PathVariable String razorpayOrderId,
            @PathVariable Long paymentId,
            @PathVariable String paymentMode) {
        log.info("Inside the Payment Success");
        Orders orders = orderRepository.findByRazorpayOrderId(razorpayOrderId).
                orElseThrow(() -> new IllegalArgumentException("Razorpay order id not found"));

        orders.setStatus(OrderStatus.PENDING);
        orders.setPaymentMode(paymentMode);
        orderRepository.save(orders);
        ApiResponseDto<String> apiResponseDto = new ApiResponseDto<>();
        apiResponseDto.setMessage("Order has been Placed Successfully");
        apiResponseDto.setData("Order has been Placed Successfully");
        apiResponseDto.setStatus("SUCCESS");
        return ResponseEntity.ok(apiResponseDto);
    }

    @PostMapping("/payment-failure/{razorpayOrderId}")
    public ResponseEntity<ApiResponseDto<String>> paymentFailureHandle(
            @PathVariable String razorpayOrderId) {

        log.info("Inside the Payment Failure");

        Orders orders = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Razorpay order id not found"));
        if (!orders.getStatus().equals(OrderStatus.PENDING)) {
            orders.setStatus(OrderStatus.PENDING);
            for (OrderItem item : orders.getItems()) {
                productClient.unreserveStockProductVariantById(
                        item.getProductVariantId(),
                        item.getQuantity()
                );
            }
        }

        orderRepository.save(orders);

        ApiResponseDto<String> response = new ApiResponseDto<>();
        response.setMessage("Failed to make Payment");
        response.setData("Failed to make Payment");
        response.setStatus("FAILED");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/verify")
    public ResponseEntity<Boolean> hasUserPurchasedProduct(
            @RequestParam Long productVariantId) {

        Boolean result = orderService.hasUserPurchasedProduct(productVariantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<UserOrderResponseDto>> getUserOrders() {
        return ResponseEntity.ok(orderService.getOrdersForUser());
    }

}
