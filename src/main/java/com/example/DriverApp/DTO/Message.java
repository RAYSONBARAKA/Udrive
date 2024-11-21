package com.example.DriverApp.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
    private String to;
    private String message;
}
