package com.learning.orderservice.mapper;


import com.learning.orderservice.dto.OrderResponseDto;
import com.learning.orderservice.entity.Orders;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponseDto toOrderResponseDto(Orders orders);
}
