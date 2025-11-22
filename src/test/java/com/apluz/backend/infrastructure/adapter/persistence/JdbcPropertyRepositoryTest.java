package com.apluz.backend.infrastructure.adapter.persistence;

import com.apluz.backend.domain.model.Property;
import com.apluz.backend.domain.model.PropertyStatus;
import com.apluz.backend.domain.model.PropertyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para JdbcPropertyRepository
 */
@JdbcTest
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql")
class JdbcPropertyRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcPropertyRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JdbcPropertyRepository(jdbcTemplate);
        // Limpiar la tabla antes de cada test
        jdbcTemplate.update("DELETE FROM properties");
    }

    @Test
    void testSaveNewProperty() {
        // Arrange
        Property property = createTestProperty();

        // Act
        Property savedProperty = repository.save(property);

        // Assert
        assertNotNull(savedProperty.getId());
        assertEquals("Test Property", savedProperty.getTitle());
    }

    @Test
    void testFindById() {
        // Arrange
        Property property = createTestProperty();
        Property savedProperty = repository.save(property);

        // Act
        Optional<Property> foundProperty = repository.findById(savedProperty.getId());

        // Assert
        assertTrue(foundProperty.isPresent());
        assertEquals("Test Property", foundProperty.get().getTitle());
    }

    @Test
    void testFindByIdNotFound() {
        // Act
        Optional<Property> foundProperty = repository.findById(999L);

        // Assert
        assertFalse(foundProperty.isPresent());
    }

    @Test
    void testFindAll() {
        // Arrange
        repository.save(createTestProperty());
        repository.save(createTestProperty());

        // Act
        List<Property> properties = repository.findAll();

        // Assert
        assertEquals(2, properties.size());
    }

    @Test
    void testFindByCity() {
        // Arrange
        Property property1 = createTestProperty();
        property1.setCity("Madrid");
        repository.save(property1);

        Property property2 = createTestProperty();
        property2.setCity("Barcelona");
        repository.save(property2);

        // Act
        List<Property> madridProperties = repository.findByCity("Madrid");

        // Assert
        assertEquals(1, madridProperties.size());
        assertEquals("Madrid", madridProperties.get(0).getCity());
    }

    @Test
    void testFindByType() {
        // Arrange
        Property property1 = createTestProperty();
        property1.setType(PropertyType.HOUSE);
        repository.save(property1);

        Property property2 = createTestProperty();
        property2.setType(PropertyType.APARTMENT);
        repository.save(property2);

        // Act
        List<Property> houses = repository.findByType(PropertyType.HOUSE);

        // Assert
        assertEquals(1, houses.size());
        assertEquals(PropertyType.HOUSE, houses.get(0).getType());
    }

    @Test
    void testFindByStatus() {
        // Arrange
        Property property1 = createTestProperty();
        property1.setStatus(PropertyStatus.AVAILABLE);
        repository.save(property1);

        Property property2 = createTestProperty();
        property2.setStatus(PropertyStatus.SOLD);
        repository.save(property2);

        // Act
        List<Property> availableProperties = repository.findByStatus(PropertyStatus.AVAILABLE);

        // Assert
        assertEquals(1, availableProperties.size());
        assertEquals(PropertyStatus.AVAILABLE, availableProperties.get(0).getStatus());
    }

    @Test
    void testUpdateProperty() {
        // Arrange
        Property property = createTestProperty();
        Property savedProperty = repository.save(property);

        savedProperty.setTitle("Updated Title");
        savedProperty.setPrice(new BigDecimal("300000.00"));

        // Act
        Property updatedProperty = repository.save(savedProperty);

        // Assert
        assertEquals("Updated Title", updatedProperty.getTitle());
        assertEquals(new BigDecimal("300000.00"), updatedProperty.getPrice());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Property property = createTestProperty();
        Property savedProperty = repository.save(property);

        // Act
        repository.deleteById(savedProperty.getId());

        // Assert
        Optional<Property> foundProperty = repository.findById(savedProperty.getId());
        assertFalse(foundProperty.isPresent());
    }

    @Test
    void testExistsById() {
        // Arrange
        Property property = createTestProperty();
        Property savedProperty = repository.save(property);

        // Act & Assert
        assertTrue(repository.existsById(savedProperty.getId()));
        assertFalse(repository.existsById(999L));
    }

    private Property createTestProperty() {
        Property property = new Property();
        property.setTitle("Test Property");
        property.setDescription("Test description");
        property.setType(PropertyType.HOUSE);
        property.setStatus(PropertyStatus.AVAILABLE);
        property.setPrice(new BigDecimal("250000.00"));
        property.setAddress("123 Test St");
        property.setCity("Madrid");
        property.setState("Madrid");
        property.setZipCode("28001");
        property.setArea(150.0);
        property.setBedrooms(3);
        property.setBathrooms(2);
        property.setParkingSpaces(2);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        return property;
    }
}

