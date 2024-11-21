package com.example.DriverApp.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverSocketDto {
    private Long id;
    private String driverName;
    private String eta;
}
