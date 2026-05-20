package com.learning.orderservice.service.implementation;

import com.learning.orderservice.dto.types.ShipmentStatus;
import com.learning.orderservice.entity.Shipment;
import com.learning.orderservice.entity.ShipmentStatusHistory;
import com.learning.orderservice.repository.ShipmentRepository;
import com.learning.orderservice.repository.ShipmentStatusRepository;
import com.learning.orderservice.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShippingServiceImpl implements ShippingService {
    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentStatusRepository statusRepository;

    public Shipment createShipment(Shipment shipment) {

        // simple cost calculation
        double cost = 50 + (shipment.getWeight() * 10);
        shipment.setShippingCost(cost);

        // estimated delivery (5 days)
        shipment.setEstimatedDeliveryDate(LocalDate.now().plusDays(5));

        Shipment saved = shipmentRepository.save(shipment);

        // initial status
        ShipmentStatusHistory status = new ShipmentStatusHistory();
        status.setShipment(saved);
        status.setStatus(ShipmentStatus.CREATED);
        status.setTimestamp(LocalDateTime.now());

        statusRepository.save(status);

        return saved;
    }

    public void updateStatus(Long shipmentId, ShipmentStatus newStatus) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment Id does not exist"));
        ShipmentStatusHistory status = new ShipmentStatusHistory();

        status.setShipment(shipment);
        status.setStatus(newStatus);
        status.setTimestamp(LocalDateTime.now());

        statusRepository.save(status);
    }

    public List<ShipmentStatusHistory> getTracking(Long shipmentId) {
        Shipment shipment =shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment Id does not exist"));

        return statusRepository.findByShipment(shipment);
    }
}
