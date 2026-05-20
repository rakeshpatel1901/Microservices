package com.learning.orderservice.controller;

import com.learning.orderservice.dto.types.ShipmentStatus;
import com.learning.orderservice.entity.Shipment;
import com.learning.orderservice.entity.ShipmentStatusHistory;
import com.learning.orderservice.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @PostMapping("/create")
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shippingService.createShipment(shipment);
    }

    @PostMapping("/status")
    public String updateStatus(@RequestParam Long shipmentId,
                               @RequestParam ShipmentStatus status) {
        shippingService.updateStatus(shipmentId, status);
        return "Status Updated";
    }

    @GetMapping("/track/{shipmentId}")
    public List<ShipmentStatusHistory> track(@PathVariable Long shipmentId) {
        return shippingService.getTracking(shipmentId);
    }
}