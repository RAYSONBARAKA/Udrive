package com.example.DriverApp.SocketIo;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.URISyntaxException;

@Configuration
public class SocketIOClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketIOClient.class);

    @Bean
    @Scope("singleton")
    public Socket createSocket() {
        try {
            // Configure options
            IO.Options options = new IO.Options();
            options.query = "deviceToken=yourDeviceToken";  
            options.reconnection = true;
            options.reconnectionAttempts = 5;
            options.reconnectionDelay = 2000;

            // Create a Socket.IO connection
            Socket socket = IO.socket("https://webserver-xqjo.onrender.com", options);

            // Listen for connection
            socket.on(Socket.EVENT_CONNECT, args -> {
                logger.info("Connected to the server!");
                socket.emit("message", "Hello from Java client!");
            });

            // Listen for disconnection
            socket.on(Socket.EVENT_DISCONNECT, args -> logger.warn("Disconnected from the server."));

            // Listen for connection errors
            socket.on(Socket.EVENT_CONNECT_ERROR, args -> logger.error("Connection error: " + args[0]));

            socket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> logger.error("Connection timed out."));

            // Listen for custom events
            socket.on("message", args -> logger.info("Message from server: {}", args[0]));
            socket.on("rideAccepted", args -> logger.info("Ride Accepted Notification: {}", args[0]));

            // Connect to the server
            socket.connect();

            return socket;

        } catch (URISyntaxException e) {
            logger.error("URI syntax error during socket initialization", e);
            return null;
        }
    }
}
