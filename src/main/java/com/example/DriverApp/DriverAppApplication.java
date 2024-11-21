package com.example.DriverApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.cloudinary.Cloudinary;
import com.example.DriverApp.Utility.JwtUtil;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

@SpringBootApplication
@EnableScheduling 
public class DriverAppApplication implements CommandLineRunner {

    @Autowired
    private Cloudinary cloudinary;

    public static void main(String[] args) {
        SpringApplication.run(DriverAppApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    private Socket socket;

    @Bean
    public Socket creatSocket() throws Exception {
        // Create a Socket.IO connection to the server
        this.socket = IO.socket("https://messageio.onrender.com?username=be");
        this.socket.connect();
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
        socket.on("user_disconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("User disconnected: " + args[0]);
            }
        });

        socket.on("connect_notification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("User connected: " + args[0]);
            }
        });

        // Connect to the server

        return socket;
    }


    @Override
    public void run(String... args) {
        if (cloudinary != null) {
            System.out.println("Cloudinary bean initialized successfully!");
        } else {
            System.out.println("Cloudinary bean is null.");
        }
        socket.emit("4", "Hello");
    }
}
