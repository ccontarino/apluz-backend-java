package com.apluz.backend.infrastructure.adapter.persistence;

import com.apluz.backend.domain.model.Property;
import com.apluz.backend.domain.model.PropertyStatus;
import com.apluz.backend.domain.model.PropertyType;
import com.apluz.backend.domain.port.PropertyRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio usando JDBC
 */
@Repository
public class JdbcPropertyRepository implements PropertyRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPropertyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Property> propertyRowMapper = (rs, rowNum) -> {
        Property property = new Property();
        property.setId(rs.getLong("id"));
        property.setTitle(rs.getString("title"));
        property.setDescription(rs.getString("description"));
        property.setType(PropertyType.valueOf(rs.getString("type")));
        property.setStatus(PropertyStatus.valueOf(rs.getString("status")));
        property.setPrice(rs.getBigDecimal("price"));
        property.setAddress(rs.getString("address"));
        property.setCity(rs.getString("city"));
        property.setState(rs.getString("state"));
        property.setZipCode(rs.getString("zip_code"));
        property.setArea(rs.getDouble("area"));
        property.setBedrooms(rs.getInt("bedrooms"));
        property.setBathrooms(rs.getInt("bathrooms"));
        property.setParkingSpaces(rs.getInt("parking_spaces"));
        property.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        property.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return property;
    };

    @Override
    public Property save(Property property) {
        if (property.getId() == null) {
            return insert(property);
        } else {
            return update(property);
        }
    }

    private Property insert(Property property) {
        String sql = """
            INSERT INTO properties (title, description, type, status, price, address, 
                                   city, state, zip_code, area, bedrooms, bathrooms, 
                                   parking_spaces, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, property.getTitle());
            ps.setString(2, property.getDescription());
            ps.setString(3, property.getType().name());
            ps.setString(4, property.getStatus().name());
            ps.setBigDecimal(5, property.getPrice());
            ps.setString(6, property.getAddress());
            ps.setString(7, property.getCity());
            ps.setString(8, property.getState());
            ps.setString(9, property.getZipCode());
            ps.setDouble(10, property.getArea());
            ps.setInt(11, property.getBedrooms());
            ps.setInt(12, property.getBathrooms());
            ps.setInt(13, property.getParkingSpaces());
            ps.setTimestamp(14, Timestamp.valueOf(property.getCreatedAt()));
            ps.setTimestamp(15, Timestamp.valueOf(property.getUpdatedAt()));
            return ps;
        }, keyHolder);

        property.setId(keyHolder.getKey().longValue());
        return property;
    }

    private Property update(Property property) {
        String sql = """
            UPDATE properties 
            SET title = ?, description = ?, type = ?, status = ?, price = ?, 
                address = ?, city = ?, state = ?, zip_code = ?, area = ?, 
                bedrooms = ?, bathrooms = ?, parking_spaces = ?, updated_at = ?
            WHERE id = ?
            """;

        jdbcTemplate.update(sql,
            property.getTitle(),
            property.getDescription(),
            property.getType().name(),
            property.getStatus().name(),
            property.getPrice(),
            property.getAddress(),
            property.getCity(),
            property.getState(),
            property.getZipCode(),
            property.getArea(),
            property.getBedrooms(),
            property.getBathrooms(),
            property.getParkingSpaces(),
            Timestamp.valueOf(property.getUpdatedAt()),
            property.getId()
        );

        return property;
    }

    @Override
    public Optional<Property> findById(Long id) {
        String sql = "SELECT * FROM properties WHERE id = ?";
        List<Property> properties = jdbcTemplate.query(sql, propertyRowMapper, id);
        return properties.isEmpty() ? Optional.empty() : Optional.of(properties.get(0));
    }

    @Override
    public List<Property> findAll() {
        String sql = "SELECT * FROM properties ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, propertyRowMapper);
    }

    @Override
    public List<Property> findByCity(String city) {
        String sql = "SELECT * FROM properties WHERE city = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, propertyRowMapper, city);
    }

    @Override
    public List<Property> findByType(PropertyType type) {
        String sql = "SELECT * FROM properties WHERE type = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, propertyRowMapper, type.name());
    }

    @Override
    public List<Property> findByStatus(PropertyStatus status) {
        String sql = "SELECT * FROM properties WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, propertyRowMapper, status.name());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM properties WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM properties WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}

