package com.mahalaxmi.authservice.service.implementation;




import com.mahalaxmi.authservice.dto.AdminUserDTO;
import com.mahalaxmi.authservice.dto.UserOrderStatsDTO;
import com.mahalaxmi.authservice.entity.Users;
import com.mahalaxmi.authservice.externalservices.OrderStatsClient;
import com.mahalaxmi.authservice.repository.AuthRepository;

import com.mahalaxmi.authservice.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AuthRepository usersRepository;
    private final OrderStatsClient orderStatsClient;

    public Page<AdminUserDTO> getUsers(String search, int page, int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 20),
                Sort.by(Sort.Direction.DESC, "createdAt"));


        Page<Users> usersPage = (search != null && !search.isBlank())
                ? usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search.trim(), search.trim(), pageable)
                : usersRepository.findAll(pageable);

        List<Users> users = usersPage.getContent();
        if (users.isEmpty()) return Page.empty(pageable);

        // 2. Bulk-fetch order stats from order-service (one Feign call)
        List<Long> userIds = users.stream().map(Users::getUserId).toList();
        Map<Long, UserOrderStatsDTO> statsMap = fetchStatsMap(userIds);

        // 3. Map to DTOs
        List<AdminUserDTO> dtos = users.stream().map(u -> {
            UserOrderStatsDTO stats = statsMap.getOrDefault(u.getUserId(),
                    new UserOrderStatsDTO(u.getUserId(), 0, BigDecimal.ZERO));
            return AdminUserDTO.builder()
                    .userId(u.getUserId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .countryCode(u.getCountryCode())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .createdAt(u.getCreatedAt())
                    .orderCount(stats.getOrderCount())
                    .totalSpent(stats.getTotalSpent())
                    .build();
        }).toList();

        return new PageImpl<>(dtos, pageable, usersPage.getTotalElements());
    }

    public long getTotalUserCount() {
        return usersRepository.count();
    }

    // ── Safe Feign call — degrades gracefully if order-service is down ─────
    private Map<Long, UserOrderStatsDTO> fetchStatsMap(List<Long> userIds) {
        try {
            List<UserOrderStatsDTO> stats = orderStatsClient.getUserStats(userIds);
            return stats.stream().collect(
                    Collectors.toMap(UserOrderStatsDTO::getUserId, Function.identity()));
        } catch (Exception ex) {
            log.warn("Could not fetch order stats from order-service: {}", ex.getMessage());
            return Collections.emptyMap(); // table still renders, just shows 0s
        }
    }
}
