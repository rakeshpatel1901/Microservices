package com.mahalaxmi.authservice.externalservices;



import com.mahalaxmi.authservice.dto.UserOrderStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// "order-service" must match spring.application.name in order-service
@FeignClient(name = "order-service", path = "/api/admin/internal/orders")
public interface OrderStatsClient {

    @GetMapping("/user-stats")
    List<UserOrderStatsDTO> getUserStats(@RequestParam("userIds") List<Long> userIds);
}

