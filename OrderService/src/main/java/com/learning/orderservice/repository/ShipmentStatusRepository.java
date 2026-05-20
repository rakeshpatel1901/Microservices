package com.learning.orderservice.repository;


import com.learning.orderservice.entity.Shipment;
import com.learning.orderservice.entity.ShipmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatusHistory, Long> {
    List<ShipmentStatusHistory> findByShipment(Shipment shipment);
    List<ShipmentStatusHistory> findByShipment_ShipmentIdOrderByTimestampAsc(Long shipmentId);
}