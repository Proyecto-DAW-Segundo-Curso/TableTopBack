package com.example.TableTop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.TableTop.model.Event;
import com.example.TableTop.model.User;
import com.example.TableTop.repository.EventRepository;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event) {
        // Lógica para crear un evento
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public void joinEvent(Long eventId, User user) {
        // Lógica para que un usuario se apunte a un evento
    }

    public void leaveEvent(Long eventId, User user) {
        // Lógica para que un usuario se quite de un evento
    }
} 