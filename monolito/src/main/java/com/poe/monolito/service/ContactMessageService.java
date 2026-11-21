package com.poe.monolito.service;

import com.poe.monolito.dto.ContactRequest;
import com.poe.monolito.dto.ContactResponse;
import com.poe.monolito.dto.UpdateMessageStatusRequest;
import com.poe.monolito.entity.ContactMessage;
import com.poe.monolito.entity.enums.MessageStatus;
import com.poe.monolito.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    // Crear un nuevo mensaje desde el formulario de contacto (Público)
    @Transactional
    public ContactResponse createMessage(ContactRequest request) {
        ContactMessage contactMessage = ContactMessage.builder()
                .name(request.name())
                .email(request.email())
                .subject(request.subject())
                .message(request.message())
                .status(MessageStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        ContactMessage saved = contactMessageRepository.save(contactMessage);
        return mapToResponse(saved);
    }

    // Listar mensajes con filtro opcional por estado (Admin)
    @Transactional(readOnly = true)
    public List<ContactResponse> getAllMessages(MessageStatus status) {
        List<ContactMessage> messages = (status != null)
                ? contactMessageRepository.findByStatusOrderByCreatedAtDesc(status)
                : contactMessageRepository.findAllByOrderByCreatedAtDesc();

        return messages.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Obtener detalle de un mensaje por ID (Admin)
    @Transactional(readOnly = true)
    public ContactResponse getMessageById(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje de soporte no encontrado con ID: " + id));
        return mapToResponse(message);
    }

    // Cambiar estado del ticket: UNREAD -> IN_REVIEW -> RESOLVED (Admin)
    @Transactional
    public ContactResponse updateStatus(Long id, UpdateMessageStatusRequest request) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje de soporte no encontrado con ID: " + id));

        message.setStatus(request.status());
        ContactMessage updated = contactMessageRepository.save(message);
        return mapToResponse(updated);
    }

    private ContactResponse mapToResponse(ContactMessage entity) {
        return new ContactResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getSubject(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}