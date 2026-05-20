package com.learning.orderservice.entity;

import com.learning.orderservice.dto.types.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipmentId;

    private Long orderId;

    private Double weight;
    private Double shippingCost;

    private LocalDate estimatedDeliveryDate;
    private ShipmentStatus currentStatus;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private ShippingAddress shippingAddress;
}
