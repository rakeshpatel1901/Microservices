package com.learning.orderservice.service;

import com.learning.orderservice.dto.types.ShipmentStatus;
import com.learning.orderservice.entity.Shipment;
import com.learning.orderservice.entity.ShipmentStatusHistory;

import java.util.List;

public interface ShippingService {
    public Shipment createShipment(Shipment shipment);
    public void updateStatus(Long shipmentId, ShipmentStatus newStatus);
    public List<ShipmentStatusHistory> getTracking(Long shipmentId);

}
