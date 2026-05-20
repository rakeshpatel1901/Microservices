package com.learning.orderservice.externalservices;

import com.learning.orderservice.config.FeignConfig;
import com.learning.orderservice.dto.UserDetailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "AUTH-SERVICE", configuration = FeignConfig.class)
public interface AuthClient {
    @GetMapping("/api/auth/get-user")
    public Long getUserId();

    @GetMapping("/api/auth/me")
    UserDetailResponseDto getMe();

    @GetMapping("/api/auth/{userId}")
    UserDetailResponseDto getUserById(@PathVariable Long userId);
    @GetMapping("/api/auth/batch")
    List<UserDetailResponseDto> getUsersByIds(@RequestParam("ids") List<Long> ids);
}
