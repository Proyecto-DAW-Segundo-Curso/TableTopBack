package com.example.TableTop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.TableTop.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByParticipants_Id(Long userId);
} 