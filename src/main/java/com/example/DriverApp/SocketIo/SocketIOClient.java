package com.example.DriverApp.SocketIo;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.URISyntaxException;

@Configuration
public class SocketIOClient {

     @Bean
    @Scope("singleton") 
    public Socket creaSocket() {
        try {
            // Create a Socket.IO connection to the server
            Socket socket = IO.socket("http://localhost:9092");

            // Listen for connection
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected to the server!");
                    socket.emit("message", "Hello from Java client!"); 
                }
            });

            // Listen for "message" event from server
            socket.on("message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Message from server: " + args[0]);
                }
            });

            // Listen for the custom "rideAccepted" event
            socket.on("rideAccepted", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Ride Accepted Notification: " + args[0]);
                }
            });

            // Connect to the server
            socket.connect();

            return socket;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null; 
    }

}
