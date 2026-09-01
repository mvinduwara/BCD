package com.globaltrade.scm.session.shipment;

import com.globaltrade.scm.common.dto.ShipmentDTO;
import com.globaltrade.scm.entity.Shipment;
import com.globaltrade.scm.entity.ShipmentStatus;
import com.globaltrade.scm.entity.Vendor;
import com.globaltrade.scm.exception.ShipmentNotFoundException;
import com.globaltrade.scm.exception.VendorNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceBeanTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ShipmentServiceBean shipmentService;

    @Test
    void findAllReturnsMappedDtos() {
        Vendor vendor = new Vendor();
        setId(vendor, 1L);

        Shipment shipment = new Shipment();
        setId(shipment, 10L);
        shipment.setTrackingNumber("GT-999999");
        shipment.setOrigin("Colombo, LK");
        shipment.setDestination("Rotterdam, NL");
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setVendor(vendor);

        TypedQuery<Shipment> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Shipment.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(shipment));

        List<ShipmentDTO> result = shipmentService.findAll();

        assertEquals(1, result.size());
        assertEquals("GT-999999", result.get(0).trackingNumber());
        assertEquals("IN_TRANSIT", result.get(0).status());
    }

    @Test
    void findByIdThrowsWhenShipmentMissing() {
        when(entityManager.find(Shipment.class, 404L)).thenReturn(null);

        assertThrows(ShipmentNotFoundException.class, () -> shipmentService.findById(404L));
    }

    @Test
    void createThrowsWhenVendorMissing() {
        ShipmentDTO input = new ShipmentDTO(null, "GT-1", "A", "B", null,
                LocalDateTime.now(), LocalDateTime.now(), 999L, 1L);
        when(entityManager.find(Vendor.class, 999L)).thenReturn(null);

        assertThrows(VendorNotFoundException.class, () -> shipmentService.create(input));
    }

    @Test
    void updateStatusChangesManagedEntityWithoutExplicitPersist() throws ShipmentNotFoundException {
        Shipment shipment = new Shipment();
        setId(shipment, 5L);
        shipment.setVendor(new Vendor());
        shipment.setStatus(ShipmentStatus.PENDING);
        when(entityManager.find(Shipment.class, 5L)).thenReturn(shipment);

        shipmentService.updateStatus(5L, ShipmentStatus.DELIVERED);

        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
    }

    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}