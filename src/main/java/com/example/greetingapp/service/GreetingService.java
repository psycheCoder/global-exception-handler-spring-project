package com.example.greetingapp.service;

import com.example.greetingapp.exception.BadRequestException;
import com.example.greetingapp.exception.ResourceNotFoundException;
import com.example.greetingapp.model.Greeting;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GreetingService {

    private final Map<Long, String> greetings = new HashMap<>();

    public GreetingService() {
        greetings.put(1L, "Hello!");
        greetings.put(2L, "Greetings, friend!");
        greetings.put(3L, "Welcome!");
    }

    public Greeting getGreeting(String idStr) {
        try {
            Long id = Long.parseLong(idStr);
            String message = greetings.get(id);
            if (message == null) {
                throw new ResourceNotFoundException("Greeting with ID " + id + " not found.");
            }
            return new Greeting(id, message);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid ID format. Please provide a valid number.");
        }
    }
}