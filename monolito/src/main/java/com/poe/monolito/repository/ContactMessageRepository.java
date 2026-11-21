package com.poe.monolito.repository;

import com.poe.monolito.entity.ContactMessage;
import com.poe.monolito.entity.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    // Listar todos los mensajes ordenados del más reciente al más antiguo
    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    // Filtrar por estado (ej: solo los "UNREAD" que requieren atención)
    List<ContactMessage> findByStatusOrderByCreatedAtDesc(MessageStatus status);
}