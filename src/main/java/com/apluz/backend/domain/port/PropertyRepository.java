package com.apluz.backend.domain.port;

import com.apluz.backend.domain.model.Property;
import com.apluz.backend.domain.model.PropertyStatus;
import com.apluz.backend.domain.model.PropertyType;

import java.util.List;
import java.util.Optional;

/**
 * Puerto (interface) del repositorio de propiedades
 * Define el contrato que debe implementar la infraestructura
 */
public interface PropertyRepository {

    Property save(Property property);

    Optional<Property> findById(Long id);

    List<Property> findAll();

    List<Property> findByCity(String city);

    List<Property> findByType(PropertyType type);

    List<Property> findByStatus(PropertyStatus status);

    void deleteById(Long id);

    boolean existsById(Long id);
}

