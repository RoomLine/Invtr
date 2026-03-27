package com.invtr.equipmentservice.dto;

import com.invtr.equipmentservice.enums.EquipmentCondition;
import com.invtr.equipmentservice.enums.EquipmentStatus;
import com.invtr.equipmentservice.enums.EquipmentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentResponse {

    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

<<<<<<< HEAD
    @Size(max = 50, message = "Serial number must be at most 50 characters")
    private String serialNumber;

=======
>>>>>>> origin/main
    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;

    @NotNull(message = "Condition cannot be null")
    @Enumerated(EnumType.STRING)
    private EquipmentCondition condition;

    @NotNull(message = "Type cannot be null")
    @Enumerated(EnumType.STRING)
    private EquipmentType type;

    @Size(max = 255, message = "QR code URL must be at most 255 characters")
    private String qrCodeUrl;

    @NotBlank(message = "Location cannot be blank")
    @Size(max = 100, message = "Location must be at most 100 characters")
    private String location;

//    @NotNull(message = "Sensitive flag cannot be null")
    private Boolean isSensitive; // TODO: check if we want to include this field

    @NotNull(message = "Created at timestamp cannot be null")
    private LocalDateTime createdAt;

    @Size(max = 255, message = "Photo URL must be at most 255 characters")
    private String photoUrl;
}