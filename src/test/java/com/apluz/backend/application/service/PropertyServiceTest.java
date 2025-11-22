package com.apluz.backend.application.service;

import com.apluz.backend.domain.model.Property;
import com.apluz.backend.domain.model.PropertyStatus;
import com.apluz.backend.domain.model.PropertyType;
import com.apluz.backend.domain.port.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PropertyService
 */
@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyService propertyService;

    private Property testProperty;

    @BeforeEach
    void setUp() {
        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setTitle("Beautiful House");
        testProperty.setDescription("A beautiful house in the city");
        testProperty.setType(PropertyType.HOUSE);
        testProperty.setStatus(PropertyStatus.AVAILABLE);
        testProperty.setPrice(new BigDecimal("250000.00"));
        testProperty.setAddress("123 Main St");
        testProperty.setCity("Madrid");
        testProperty.setState("Madrid");
        testProperty.setZipCode("28001");
        testProperty.setArea(150.0);
        testProperty.setBedrooms(3);
        testProperty.setBathrooms(2);
        testProperty.setParkingSpaces(2);
        testProperty.setCreatedAt(LocalDateTime.now());
        testProperty.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateProperty() {
        // Arrange
        Property newProperty = new Property();
        newProperty.setTitle("New Property");
        newProperty.setDescription("Test description");
        newProperty.setType(PropertyType.APARTMENT);
        newProperty.setPrice(new BigDecimal("150000.00"));
        newProperty.setAddress("456 Test Ave");
        newProperty.setCity("Barcelona");
        newProperty.setState("Cataluña");
        newProperty.setZipCode("08001");
        newProperty.setArea(80.0);
        newProperty.setBedrooms(2);
        newProperty.setBathrooms(1);
        newProperty.setParkingSpaces(1);

        when(propertyRepository.save(any(Property.class))).thenReturn(newProperty);

        // Act
        Property createdProperty = propertyService.createProperty(newProperty);

        // Assert
        assertNotNull(createdProperty);
        assertEquals(PropertyStatus.AVAILABLE, createdProperty.getStatus());
        assertNotNull(createdProperty.getCreatedAt());
        assertNotNull(createdProperty.getUpdatedAt());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testGetPropertyById() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        // Act
        Optional<Property> foundProperty = propertyService.getPropertyById(1L);

        // Assert
        assertTrue(foundProperty.isPresent());
        assertEquals("Beautiful House", foundProperty.get().getTitle());
        verify(propertyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllProperties() {
        // Arrange
        List<Property> properties = Arrays.asList(testProperty, testProperty);
        when(propertyRepository.findAll()).thenReturn(properties);

        // Act
        List<Property> foundProperties = propertyService.getAllProperties();

        // Assert
        assertEquals(2, foundProperties.size());
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void testGetPropertiesByCity() {
        // Arrange
        when(propertyRepository.findByCity("Madrid")).thenReturn(Arrays.asList(testProperty));

        // Act
        List<Property> properties = propertyService.getPropertiesByCity("Madrid");

        // Assert
        assertEquals(1, properties.size());
        assertEquals("Madrid", properties.get(0).getCity());
        verify(propertyRepository, times(1)).findByCity("Madrid");
    }

    @Test
    void testUpdateProperty() {
        // Arrange
        Property updatedProperty = new Property();
        updatedProperty.setTitle("Updated Title");
        updatedProperty.setDescription("Updated description");
        updatedProperty.setType(PropertyType.HOUSE);
        updatedProperty.setStatus(PropertyStatus.SOLD);
        updatedProperty.setPrice(new BigDecimal("300000.00"));
        updatedProperty.setAddress("123 Main St");
        updatedProperty.setCity("Madrid");
        updatedProperty.setState("Madrid");
        updatedProperty.setZipCode("28001");
        updatedProperty.setArea(150.0);
        updatedProperty.setBedrooms(3);
        updatedProperty.setBathrooms(2);
        updatedProperty.setParkingSpaces(2);

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // Act
        Property result = propertyService.updateProperty(1L, updatedProperty);

        // Assert
        assertNotNull(result);
        verify(propertyRepository, times(1)).findById(1L);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testUpdatePropertyNotFound() {
        // Arrange
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PropertyService.PropertyNotFoundException.class, () -> {
            propertyService.updateProperty(999L, testProperty);
        });
    }

    @Test
    void testDeleteProperty() {
        // Arrange
        when(propertyRepository.existsById(1L)).thenReturn(true);

        // Act
        propertyService.deleteProperty(1L);

        // Assert
        verify(propertyRepository, times(1)).existsById(1L);
        verify(propertyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePropertyNotFound() {
        // Arrange
        when(propertyRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(PropertyService.PropertyNotFoundException.class, () -> {
            propertyService.deleteProperty(999L);
        });
    }

    @Test
    void testUpdatePropertyStatus() {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        // Act
        Property result = propertyService.updatePropertyStatus(1L, PropertyStatus.SOLD);

        // Assert
        assertNotNull(result);
        verify(propertyRepository, times(1)).findById(1L);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }
}

