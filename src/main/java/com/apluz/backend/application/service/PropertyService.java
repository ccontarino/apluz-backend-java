package com.apluz.backend.application.service;

import com.apluz.backend.domain.model.Property;
import com.apluz.backend.domain.model.PropertyStatus;
import com.apluz.backend.domain.model.PropertyType;
import com.apluz.backend.domain.port.PropertyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación: Gestiona la lógica de negocio de propiedades
 */
@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property createProperty(Property property) {
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        if (property.getStatus() == null) {
            property.setStatus(PropertyStatus.AVAILABLE);
        }
        return propertyRepository.save(property);
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public List<Property> getPropertiesByCity(String city) {
        return propertyRepository.findByCity(city);
    }

    public List<Property> getPropertiesByType(PropertyType type) {
        return propertyRepository.findByType(type);
    }

    public List<Property> getPropertiesByStatus(PropertyStatus status) {
        return propertyRepository.findByStatus(status);
    }

    public Property updateProperty(Long id, Property updatedProperty) {
        Optional<Property> existingProperty = propertyRepository.findById(id);
        if (existingProperty.isEmpty()) {
            throw new PropertyNotFoundException("Property with id " + id + " not found");
        }

        Property property = existingProperty.get();
        property.setTitle(updatedProperty.getTitle());
        property.setDescription(updatedProperty.getDescription());
        property.setType(updatedProperty.getType());
        property.setStatus(updatedProperty.getStatus());
        property.setPrice(updatedProperty.getPrice());
        property.setAddress(updatedProperty.getAddress());
        property.setCity(updatedProperty.getCity());
        property.setState(updatedProperty.getState());
        property.setZipCode(updatedProperty.getZipCode());
        property.setArea(updatedProperty.getArea());
        property.setBedrooms(updatedProperty.getBedrooms());
        property.setBathrooms(updatedProperty.getBathrooms());
        property.setParkingSpaces(updatedProperty.getParkingSpaces());
        property.setUpdatedAt(LocalDateTime.now());

        return propertyRepository.save(property);
    }

    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new PropertyNotFoundException("Property with id " + id + " not found");
        }
        propertyRepository.deleteById(id);
    }

    public Property updatePropertyStatus(Long id, PropertyStatus status) {
        Optional<Property> existingProperty = propertyRepository.findById(id);
        if (existingProperty.isEmpty()) {
            throw new PropertyNotFoundException("Property with id " + id + " not found");
        }

        Property property = existingProperty.get();
        property.setStatus(status);
        property.setUpdatedAt(LocalDateTime.now());

        return propertyRepository.save(property);
    }

    public static class PropertyNotFoundException extends RuntimeException {
        public PropertyNotFoundException(String message) {
            super(message);
        }
    }
}

