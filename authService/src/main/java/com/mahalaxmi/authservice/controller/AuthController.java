package com.mahalaxmi.authservice.controller;

import com.mahalaxmi.authservice.dto.*;
import com.mahalaxmi.authservice.entity.RefreshToken;
import com.mahalaxmi.authservice.exception.InvalidOtpException;
import com.mahalaxmi.authservice.externalservices.NotificationClient;
import com.mahalaxmi.authservice.repository.RefreshTokenRepository;
import com.mahalaxmi.authservice.service.AuthService;
import com.mahalaxmi.authservice.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    AuthService authService;

    AuthenticationManager authenticationManager;

    JwtService jwtService;

    StreamBridge streamBridge;

    NotificationClient notificationClient;

    RefreshTokenRepository refreshRepo;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager,
                          JwtService jwtService, StreamBridge streamBridge,
                          NotificationClient notificationClient,
                          RefreshTokenRepository refreshRepo){
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.streamBridge = streamBridge;
        this.notificationClient = notificationClient;
        this.refreshRepo = refreshRepo;
    }

    @PostMapping("/getOtp")
    public ResponseEntity<ApiResponseDTO<SendOtpRequestDto>> getOtp(@Valid @RequestBody GenerateOtpRequestDto generateOtpRequestDto){
        System.out.println(generateOtpRequestDto);

        String otp = authService.generateOtp(generateOtpRequestDto);
        String phone = generateOtpRequestDto.getCountryCode()+generateOtpRequestDto.getPhone();
        SendOtpRequestDto sendOtpRequestDto = new SendOtpRequestDto();
        sendOtpRequestDto.setCode(otp);
        sendOtpRequestDto.setPhone(phone);

       notificationClient.sendOTP(sendOtpRequestDto);

        ApiResponseDTO<SendOtpRequestDto> apiResponseDTO = new ApiResponseDTO<>();
        apiResponseDTO.setStatusCode("200");
        apiResponseDTO.setStatus("Successful");
        apiResponseDTO.setData(sendOtpRequestDto);
        apiResponseDTO.setMessage("OTP has been sent");
        return ResponseEntity.ok(apiResponseDTO);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponseDTO<Void>> verifyOtp(
            @RequestBody VerifyOtpRequestDto verifyOtpRequestDto){

        boolean status = authService.verifyOtp(verifyOtpRequestDto);
        if(!status){
            throw new InvalidOtpException("Invalid Otp or Expired");
        }
        ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>();
        apiResponseDTO.setStatus("Success");
        apiResponseDTO.setStatusCode("200");
        apiResponseDTO.setMessage("Otp Verified");
        return ResponseEntity.ok(apiResponseDTO);
    }
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@Valid  @RequestBody UserRequestDTO userRequestDto){
        String message = authService.registerUser(userRequestDto);
        streamBridge.send("registerNotification-out-0",userRequestDto.getEmail());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken != null) {
            refreshRepo.findByToken(refreshToken)
                    .ifPresent(refreshRepo::delete);
        }

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("No refresh token"));

        RefreshToken stored = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Expired refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);
        String newAccessToken = jwtService.generateToken(email);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserAuthDTO userAuthDTO,  HttpServletResponse response){
        System.out.println("Inside user Login");
        String accessToken = null;
        String refreshToken = null;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userAuthDTO.getEmail(),userAuthDTO.getPassword())
        );

        if(authentication.isAuthenticated()){
            accessToken = jwtService.generateToken(userAuthDTO.getEmail());
            refreshToken = jwtService.generateRefreshToken(userAuthDTO.getEmail());
        }
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setEmail(userAuthDTO.getEmail());
        rt.setExpiryDate(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        refreshRepo.save(rt);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // use true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);
        return ResponseEntity.ok(
                Map.of(
                        "accessToken", accessToken,
                        "message", "Login successful"
                )
        );
    }

    @GetMapping("/me")
    public UserDetailResponseDto getMe(){
        return authService.getMe();
    }

    @GetMapping("/get-user")
    public Long getUserId(){
        return authService.getUserId();
    }

    @GetMapping("/{userId}")
    UserDetailResponseDto getUserById(@PathVariable Long userId){
        return authService.getUserById(userId);
    }
    @GetMapping("/batch")
    List<UserDetailResponseDto> getUsersByIds(@RequestParam("ids") List<Long> ids){
        return authService.getUsersByIds(ids);
    }
}
