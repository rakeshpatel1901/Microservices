package com.mahalaxmi.user.externalservices;

import com.mahalaxmi.user.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "AUTH-SERVICE", configuration = FeignConfig.class)
public interface AuthClient {
    @GetMapping("/api/auth/get-user")
    public Long getUserId();
}
