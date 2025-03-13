package com.example.TableTop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.TableTop.model.Event;
import com.example.TableTop.service.EventService;
import com.example.TableTop.service.FirebaseUserService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EventController {
    
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private static final String DEV_MODE_USER_ID = "dev-user-1234";
    
    @Value("${firebase.authentication.enabled:false}")
    private boolean firebaseAuthEnabled;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FirebaseUserService firebaseUserService;

    @PostMapping("create-event")
    public Event createEvent(@RequestBody Event event, HttpServletRequest request) {
        String userId = getUserId(request);
        logger.info("Creando evento con usuario ID: {}", userId);
        return eventService.createEvent(event, userId);
    }

    @GetMapping("/")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        logger.info("Obteniendo evento con ID: {}", id);
        ResponseEntity<?> response = eventService.getEvent(id);
        logger.info("Respuesta del servicio para evento {}: {}", id, response.getStatusCode());
        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("No se encontró el evento con ID: {}", id);
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id, HttpServletRequest request) {
        String userId = getUserId(request);
        logger.info("Eliminando evento {} con usuario ID: {}", id, userId);
        return eventService.deleteEvent(id, userId);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinEvent(@PathVariable Long id, HttpServletRequest request) {
        String userId = getUserId(request);
        logger.info("Unirse al evento {} con usuario ID: {}", id, userId);
        return eventService.joinEvent(id, userId);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leaveEvent(@PathVariable Long id, HttpServletRequest request) {
        String userId = getUserId(request);
        logger.info("Salir del evento {} con usuario ID: {}", id, userId);
        return eventService.leaveEvent(id, userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent, HttpServletRequest request) {
        String userId = getUserId(request);
        logger.info("Actualizando evento {} con usuario ID: {}", id, userId);
        return eventService.updateEvent(id, updatedEvent, userId);
    }
    
    /**
     * Método centralizado para obtener el ID de usuario, ya sea de Firebase o el modo desarrollo
     */
    private String getUserId(HttpServletRequest request) {
        try {
            // Verificamos si debemos intentar autenticar con Firebase
            if (isFirebaseAvailable() && firebaseAuthEnabled) {
                return extractFirebaseUid(request);
            } else {
                logger.info("Firebase no disponible o desactivado. Usando modo desarrollo con ID: {}", DEV_MODE_USER_ID);
                return DEV_MODE_USER_ID;
            }
        } catch (Exception e) {
            logger.warn("Error al obtener el ID de usuario: {}. Usando ID de desarrollo.", e.getMessage());
            return DEV_MODE_USER_ID;
        }
    }
    
    // Método para verificar si Firebase está disponible
    private boolean isFirebaseAvailable() {
        try {
            return !FirebaseApp.getApps().isEmpty();
        } catch (Exception e) {
            logger.warn("Error al verificar si Firebase está disponible: {}", e.getMessage());
            return false;
        }
    }

    // Método para obtener el UID de Firebase desde el token
    private String extractFirebaseUid(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        
        if (token == null || token.isEmpty()) {
            logger.warn("No se encontró token de autorización en la solicitud");
            throw new RuntimeException("Token de autorización no encontrado");
        }
        
        if (!token.startsWith("Bearer ")) {
            logger.warn("Token de autorización con formato inválido: {}", token);
            throw new RuntimeException("Formato de token inválido, debe comenzar con 'Bearer '");
        }
        
        try {
            // Extraemos el token sin el prefijo "Bearer "
            String tokenValue = token.substring(7);
            logger.debug("Verificando token: {}", tokenValue.substring(0, Math.min(20, tokenValue.length())) + "...");
            
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(tokenValue);
            String uid = decodedToken.getUid();
            logger.info("Token verificado correctamente. UID: {}", uid);
            return uid;
        } catch (Exception e) {
            logger.error("Error al verificar el token de Firebase: {}", e.getMessage());
            throw new RuntimeException("Error al verificar el token de Firebase: " + e.getMessage());
        }
    }
}
