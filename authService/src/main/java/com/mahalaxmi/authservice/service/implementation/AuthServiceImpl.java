package com.mahalaxmi.authservice.service.implementation;

import com.mahalaxmi.authservice.dto.*;
import com.mahalaxmi.authservice.entity.Users;
import com.mahalaxmi.authservice.exception.EmailAlreadyExistsException;
import com.mahalaxmi.authservice.exception.EmailDoesNotExistsException;
import com.mahalaxmi.authservice.exception.PhoneAlreadyExistsException;
import com.mahalaxmi.authservice.exception.PhoneNotVerifiedException;
import com.mahalaxmi.authservice.mapper.UserMapper;
import com.mahalaxmi.authservice.repository.AuthRepository;

import com.mahalaxmi.authservice.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    AuthRepository authRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    RedisTemplate<String,String> redisTemplate;

    public AuthServiceImpl(AuthRepository authRepository, UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           RedisTemplate<String, String> redisTemplate){
        this.authRepository = authRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    @Override
    public String registerUser(UserRequestDTO userRequestDto) {

        String key = "verified:" +userRequestDto.getCountryCode()+userRequestDto.getPhone();

        String isVerified = redisTemplate.opsForValue().get(key);

        if (isVerified == null) {
            throw new PhoneNotVerifiedException("Phone number not verified");
        }
        Users users = userMapper.toUserEntity(userRequestDto);
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        if(authRepository.existsByEmail(users.getEmail())){
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        if(authRepository.existsByPhone(users.getPhone())){
            throw new PhoneAlreadyExistsException("Phone number already exists");
        }
        Users newUsers = authRepository.save(users);

        redisTemplate.delete(key);

        return "User Created Successfully!";

    }

    @Override
    public UserDetailResponseDto getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = getUserByEmail(email);
        UserDetailResponseDto dto = new UserDetailResponseDto();
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setUserId(user.getUserId());
        return dto;
    }


    @Override
    public String generateOtp(GenerateOtpRequestDto generateOtpRequestDto) {
        String otp = generateOtp();
        String phone = generateOtpRequestDto.getCountryCode()+generateOtpRequestDto.getPhone();
        redisTemplate.opsForValue().set(
                "otp:" + phone,
                otp,
                Duration.ofMinutes(5)
        );
        System.out.println("OTP: " + otp);
        return otp;
    }

    @Override
    public boolean verifyOtp(VerifyOtpRequestDto verifyOtpRequestDto) {
        String phone = verifyOtpRequestDto.getCountryCode()+verifyOtpRequestDto.getPhone();
        String storedOtp = redisTemplate.opsForValue().get("otp:" + phone);
        if (storedOtp == null || !storedOtp.equals(verifyOtpRequestDto.getCode())) {
            return false;
        }


        redisTemplate.opsForValue().set(
                "verified:" + phone,
                "true",
                Duration.ofMinutes(15)
        );

        redisTemplate.delete("otp:" + phone);

        return true;

    }

    @Override
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = getUserByEmail(email);
        return user.getUserId();
    }

    @Override
    public UserDetailResponseDto getUserById(Long userId) {
        Users users = authRepository.findById(userId).orElse(null);
        if(users != null){
            UserDetailResponseDto userDetailResponseDto = new UserDetailResponseDto();
            userDetailResponseDto.setUserId(users.getUserId());
            userDetailResponseDto.setEmail(users.getEmail());
            userDetailResponseDto.setName(users.getName());
            userDetailResponseDto.setPhone(users.getPhone());
            userDetailResponseDto.setCountryCode(users.getCountryCode());
            userDetailResponseDto.setRole(users.getRole());
            return userDetailResponseDto;
        }
        return null;
    }

    @Override
    public List<UserDetailResponseDto> getUsersByIds(List<Long> ids) {

        List<Users> users = authRepository.findAllById(ids);

        return users.stream()
                .map(user -> UserDetailResponseDto.builder()
                        .userId(user.getUserId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .countryCode(user.getCountryCode())
                        .build())
                .toList();
    }

    private String generateOtp() {
        int otp = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }
    public Users getUserByEmail(String email){
        return authRepository
                .findByEmail(email)
                .orElseThrow(() -> new EmailDoesNotExistsException("This user email does not exists"));

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = getUserByEmail(email);
        return User.builder()
                .username(users.getEmail())
                .password(users.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + users.getRole().toUpperCase())))
                .build();
    }
}
