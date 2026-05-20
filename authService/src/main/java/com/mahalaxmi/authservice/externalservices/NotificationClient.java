package com.mahalaxmi.authservice.externalservices;

import com.mahalaxmi.authservice.dto.SendOtpRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="NOTIFICATION-SERVICE")
public interface NotificationClient {
    @PostMapping("/api/notification/sendOTP")
    public void sendOTP(@RequestBody SendOtpRequestDto sendOtpRequestDto);
}
