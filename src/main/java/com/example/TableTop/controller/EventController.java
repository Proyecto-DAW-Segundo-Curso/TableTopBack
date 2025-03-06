package com.example.TableTop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.TableTop.model.Event;
import com.example.TableTop.service.EventService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping
    public Event createEvent(@RequestBody Event event, HttpServletRequest request) {
        String firebaseUid = extractFirebaseUid(request); // 🔥 Obtener el UID de Firebase
        return eventService.createEvent(event, firebaseUid);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id, HttpServletRequest request) {
        String firebaseUid = extractFirebaseUid(request);
        return eventService.deleteEvent(id, firebaseUid);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinEvent(@PathVariable Long id, HttpServletRequest request) {
        String firebaseUid = extractFirebaseUid(request);
        return eventService.joinEvent(id, firebaseUid);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leaveEvent(@PathVariable Long id, HttpServletRequest request) {
        String firebaseUid = extractFirebaseUid(request);
        return eventService.leaveEvent(id, firebaseUid);
    }

    // 🔥 Método para obtener el UID de Firebase desde el token
    private String extractFirebaseUid(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token.substring(7));
                return decodedToken.getUid();
            } catch (Exception e) {
                throw new RuntimeException("Error al verificar el token de Firebase");
            }
        }
        throw new RuntimeException("Token de autorización no encontrado o inválido");
    }
}
