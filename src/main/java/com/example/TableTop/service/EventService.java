package com.example.TableTop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.TableTop.model.Event;
import com.example.TableTop.repository.EventRepository;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event, String firebaseUid) {
        event.setCreatorId(firebaseUid); // Asigna el UID de Firebase como creador
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public ResponseEntity<String> deleteEvent(Long eventId, String firebaseUid) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            if (!event.getCreatorId().equals(firebaseUid)) {
                return ResponseEntity.status(403).body("No tienes permisos para eliminar este evento.");
            }
            eventRepository.deleteById(eventId);
            return ResponseEntity.ok("Evento eliminado con éxito.");
        }
        return ResponseEntity.status(404).body("Evento no encontrado.");
    }

    public ResponseEntity<?> joinEvent(Long eventId, String firebaseUid) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            
            // Validar si el usuario ya está en el evento
            if (event.getParticipants().contains(firebaseUid)) {
                return ResponseEntity.badRequest().body("Ya estás en este evento.");
            }

            // Validar si hay espacio en el evento
            if (event.getParticipants().size() >= event.getMaxPlayers()) {
                return ResponseEntity.badRequest().body("El evento ya está lleno.");
            }

            event.getParticipants().add(firebaseUid);
            eventRepository.save(event);
            return ResponseEntity.ok(event);
        }
        return ResponseEntity.status(404).body("Evento no encontrado.");
    }

    public ResponseEntity<?> leaveEvent(Long eventId, String firebaseUid) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            if (!event.getParticipants().contains(firebaseUid)) {
                return ResponseEntity.badRequest().body("No estás en este evento.");
            }

            event.getParticipants().remove(firebaseUid);
            eventRepository.save(event);
            return ResponseEntity.ok("Has salido del evento.");
        }
        return ResponseEntity.status(404).body("Evento no encontrado.");
    }

    public ResponseEntity<?> updateEvent(Long eventId, Event updatedEvent, String firebaseUid) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            
            // Verificar que el usuario sea el creador del evento
            if (!event.getCreatorId().equals(firebaseUid)) {
                return ResponseEntity.status(403).body("No tienes permisos para editar este evento.");
            }
            
            // Actualizar los campos del evento pero mantener el creador y los participantes
            event.setName(updatedEvent.getName());
            event.setLocation(updatedEvent.getLocation());
            event.setDateTime(updatedEvent.getDateTime());
            event.setMaxPlayers(updatedEvent.getMaxPlayers());
            
            // Si el nuevo maxPlayers es menor que la cantidad actual de participantes,
            // comprobar y advertir
            if (event.getParticipants().size() > updatedEvent.getMaxPlayers()) {
                return ResponseEntity.badRequest().body("No puedes reducir el máximo de jugadores por debajo del número actual de participantes.");
            }
            
            Event savedEvent = eventRepository.save(event);
            return ResponseEntity.ok(savedEvent);
        }
        return ResponseEntity.status(404).body("Evento no encontrado.");
    }
}
