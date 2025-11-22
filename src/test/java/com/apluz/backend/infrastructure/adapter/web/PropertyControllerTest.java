package com.apluz.backend.infrastructure.adapter.web;

import com.apluz.backend.application.service.PropertyService;
import com.apluz.backend.domain.model.Property;
import com.apluz.backend.domain.model.PropertyStatus;
import com.apluz.backend.domain.model.PropertyType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para PropertyController
 */
@WebMvcTest(PropertyController.class)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
    void testGetPropertyById() throws Exception {
        // Arrange
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(testProperty));

        // Act & Assert
        mockMvc.perform(get("/api/properties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Beautiful House"))
            .andExpect(jsonPath("$.city").value("Madrid"));
    }

    @Test
    void testGetPropertyByIdNotFound() throws Exception {
        // Arrange
        when(propertyService.getPropertyById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/properties/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllProperties() throws Exception {
        // Arrange
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(testProperty));

        // Act & Assert
        mockMvc.perform(get("/api/properties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Beautiful House"));
    }

    @Test
    void testCreateProperty() throws Exception {
        // Arrange
        when(propertyService.createProperty(any(Property.class))).thenReturn(testProperty);

        String requestBody = """
            {
                "title": "Beautiful House",
                "description": "A beautiful house in the city",
                "type": "HOUSE",
                "status": "AVAILABLE",
                "price": 250000.00,
                "address": "123 Main St",
                "city": "Madrid",
                "state": "Madrid",
                "zipCode": "28001",
                "area": 150.0,
                "bedrooms": 3,
                "bathrooms": 2,
                "parkingSpaces": 2
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/properties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Beautiful House"));
    }

    @Test
    void testUpdateProperty() throws Exception {
        // Arrange
        when(propertyService.updateProperty(eq(1L), any(Property.class))).thenReturn(testProperty);

        String requestBody = """
            {
                "title": "Updated House",
                "description": "An updated description",
                "type": "HOUSE",
                "status": "AVAILABLE",
                "price": 280000.00,
                "address": "123 Main St",
                "city": "Madrid",
                "state": "Madrid",
                "zipCode": "28001",
                "area": 150.0,
                "bedrooms": 3,
                "bathrooms": 2,
                "parkingSpaces": 2
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/properties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteProperty() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/properties/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testUpdatePropertyStatus() throws Exception {
        // Arrange
        testProperty.setStatus(PropertyStatus.SOLD);
        when(propertyService.updatePropertyStatus(1L, PropertyStatus.SOLD)).thenReturn(testProperty);

        String requestBody = """
            {
                "status": "SOLD"
            }
            """;

        // Act & Assert
        mockMvc.perform(patch("/api/properties/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SOLD"));
    }
}

