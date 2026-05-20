package com.learning.orderservice.controller.admin;




import com.learning.orderservice.dto.admin.UserOrderStatsDTO;
import com.learning.orderservice.repository.OrderRepository;
import com.learning.orderservice.dto.types.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/internal/orders")
@RequiredArgsConstructor
public class InternalOrderStatsController {

    private final OrderRepository orderRepository;


    private static final Set<OrderStatus> EXCLUDED = Set.of(
            OrderStatus.CANCELLED, OrderStatus.RETURNED
    );


    @GetMapping("/user-stats")
    public List<UserOrderStatsDTO> getUserStats(
            @RequestParam("userIds") List<Long> userIds
    ) {
        // Raw JPQL projection query — add to OrderRepository
        List<Object[]> rows = orderRepository.findUserOrderStats(userIds, EXCLUDED);


        Map<Long, UserOrderStatsDTO> resultMap = rows.stream().collect(Collectors.toMap(
                row -> (Long)       row[0],
                row -> new UserOrderStatsDTO(
                        (Long)       row[0],
                        ((Long)      row[1]).intValue(),
                        (BigDecimal) row[2]
                )
        ));

        // Fill in zeros for users with no orders
        return userIds.stream()
                .map(id -> resultMap.getOrDefault(id,
                        new UserOrderStatsDTO(id, 0, BigDecimal.ZERO)))
                .collect(Collectors.toList());
    }
}

