package com.learning.orderservice.service.implementation;

import com.learning.orderservice.dto.ApiResponseDto;
import com.learning.orderservice.dto.OrderResponseDto;
import com.learning.orderservice.dto.ProductVariantRequestDto;
import com.learning.orderservice.dto.RazorpayResponseDto;
import com.learning.orderservice.dto.types.OrderStatus;
import com.learning.orderservice.entity.Orders;
import com.learning.orderservice.externalservices.PaymentClient;
import com.learning.orderservice.mapper.OrderMapper;
import com.learning.orderservice.repository.OrderRepository;
import com.learning.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final PaymentClient paymentClient;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            PaymentClient paymentClient){
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.paymentClient = paymentClient;

    }

    @Override
    public OrderResponseDto createOrder(Integer quantity, ApiResponseDto<ProductVariantRequestDto> response) throws Exception {

        Orders orders = new Orders();
        orders.setQuantity(quantity);
        orders.setStatus(OrderStatus.PENDING_PAYMENT);
        orders.setProductVariantId(response.getData().getProductVariantId());
        orders.setAmount(response.getData().getSellingPrice().multiply(BigDecimal.valueOf(quantity)));

        orderRepository.save(orders);



        RazorpayResponseDto paymentResponse =
                paymentClient.createOrder(orders.getOrderId(), orders.getAmount());

        orders.setRazorpayOrderId(paymentResponse.getRazorpayOrderId());
        orderRepository.save(orders);

        return orderMapper.toOrderResponseDto(orders);

    }
}
