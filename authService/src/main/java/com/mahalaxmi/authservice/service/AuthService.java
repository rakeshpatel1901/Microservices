package com.mahalaxmi.authservice.service;

import com.mahalaxmi.authservice.dto.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface AuthService {

    String registerUser(UserRequestDTO userRequestDto);
    UserDetailResponseDto getMe();
    String generateOtp(GenerateOtpRequestDto generateOtpRequestDto);
    boolean verifyOtp(VerifyOtpRequestDto verifyOtpRequestDto);
    Long getUserId();
    UserDetailResponseDto getUserById(@PathVariable Long userId);
    List<UserDetailResponseDto> getUsersByIds(@RequestParam("ids") List<Long> ids);
}
