package com.apluz.backend.infrastructure.adapter.web.dto;

import com.apluz.backend.domain.model.PropertyStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el estado de una propiedad
 */
public class PropertyStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private PropertyStatus status;

    public PropertyStatusUpdateRequest() {
    }

    public PropertyStatusUpdateRequest(PropertyStatus status) {
        this.status = status;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }
}

