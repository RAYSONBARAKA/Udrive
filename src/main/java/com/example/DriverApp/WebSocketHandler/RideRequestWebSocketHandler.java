package com.example.DriverApp.WebSocketHandler;

import com.example.DriverApp.DTO.RideRequestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class RideRequestWebSocketHandler extends TextWebSocketHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    private static Map<String, WebSocketSession> customerSessions = new HashMap<>();
    private static Map<String, WebSocketSession> driverSessions = new HashMap<>();

    public static void registerCustomerSession(String customerId, WebSocketSession session) {
        customerSessions.put(customerId, session);
    }

    public static void registerDriverSession(String driverId, WebSocketSession session) {
        driverSessions.put(driverId, session);
    }

    public static WebSocketSession getCustomerSession(String customerId) {
        return customerSessions.get(customerId);
    }

    public static WebSocketSession getDriverSession(String driverId) {
        return driverSessions.get(driverId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        RideRequestMessage rideRequestMessage = objectMapper.readValue(payload, RideRequestMessage.class);

        if (rideRequestMessage.getType().equals("request")) {
            // Register customer session
            registerCustomerSession(rideRequestMessage.getCustomerId(), session);
        } else if (rideRequestMessage.getType().equals("accept")) {
            // Register driver session
            registerDriverSession(rideRequestMessage.getDriverId(), session);
        }
    }
}
