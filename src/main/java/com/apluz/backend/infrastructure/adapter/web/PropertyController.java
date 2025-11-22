package com.apluz.backend.infrastructure.adapter.web;

import com.apluz.backend.application.service.PropertyService;
import com.apluz.backend.domain.model.Property;
import com.apluz.backend.domain.model.PropertyStatus;
import com.apluz.backend.domain.model.PropertyType;
import com.apluz.backend.infrastructure.adapter.web.dto.PropertyRequest;
import com.apluz.backend.infrastructure.adapter.web.dto.PropertyResponse;
import com.apluz.backend.infrastructure.adapter.web.dto.PropertyStatusUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de propiedades
 */
@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody PropertyRequest request) {
        Property property = mapToEntity(request);
        Property createdProperty = propertyService.createProperty(property);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(createdProperty));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
            .map(property -> ResponseEntity.ok(mapToResponse(property)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) PropertyStatus status) {

        List<Property> properties;

        if (city != null) {
            properties = propertyService.getPropertiesByCity(city);
        } else if (type != null) {
            properties = propertyService.getPropertiesByType(type);
        } else if (status != null) {
            properties = propertyService.getPropertiesByStatus(status);
        } else {
            properties = propertyService.getAllProperties();
        }

        List<PropertyResponse> response = properties.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequest request) {
        try {
            Property property = mapToEntity(request);
            Property updatedProperty = propertyService.updateProperty(id, property);
            return ResponseEntity.ok(mapToResponse(updatedProperty));
        } catch (PropertyService.PropertyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PropertyResponse> updatePropertyStatus(
            @PathVariable Long id,
            @Valid @RequestBody PropertyStatusUpdateRequest request) {
        try {
            Property updatedProperty = propertyService.updatePropertyStatus(id, request.getStatus());
            return ResponseEntity.ok(mapToResponse(updatedProperty));
        } catch (PropertyService.PropertyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.noContent().build();
        } catch (PropertyService.PropertyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Mappers
    private Property mapToEntity(PropertyRequest request) {
        Property property = new Property();
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setType(request.getType());
        property.setStatus(request.getStatus());
        property.setPrice(request.getPrice());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setZipCode(request.getZipCode());
        property.setArea(request.getArea());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setParkingSpaces(request.getParkingSpaces());
        return property;
    }

    private PropertyResponse mapToResponse(Property property) {
        return new PropertyResponse(
            property.getId(),
            property.getTitle(),
            property.getDescription(),
            property.getType(),
            property.getStatus(),
            property.getPrice(),
            property.getAddress(),
            property.getCity(),
            property.getState(),
            property.getZipCode(),
            property.getArea(),
            property.getBedrooms(),
            property.getBathrooms(),
            property.getParkingSpaces(),
            property.getCreatedAt(),
            property.getUpdatedAt()
        );
    }
}

