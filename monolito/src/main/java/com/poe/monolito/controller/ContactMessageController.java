package com.poe.monolito.controller;

import com.poe.monolito.dto.ContactRequest;
import com.poe.monolito.dto.ContactResponse;
import com.poe.monolito.dto.UpdateMessageStatusRequest;
import com.poe.monolito.entity.enums.MessageStatus;
import com.poe.monolito.service.ContactMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    // Formulario de contacto en React: POST /api/v1/contact
    @PostMapping
    public ResponseEntity<ContactResponse> sendMessage(@Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactMessageService.createMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Bandeja de soporte (Admin): GET /api/v1/contact?status=UNREAD
    @GetMapping
    public ResponseEntity<List<ContactResponse>> getMessages(
            @RequestParam(required = false) MessageStatus status) {
        return ResponseEntity.ok(contactMessageService.getAllMessages(status));
    }

    // Detalle de un mensaje específico: GET /api/v1/contact/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMessageService.getMessageById(id));
    }

    // Actualizar estado del ticket: PATCH /api/v1/contact/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ContactResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMessageStatusRequest request) {
        return ResponseEntity.ok(contactMessageService.updateStatus(id, request));
    }

    // Eliminar mensaje/ticket de soporte (Admin): DELETE /api/v1/contact/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMessage(@PathVariable Long id) {
        contactMessageService.deleteMessage(id);
        return ResponseEntity.ok(Map.of("message", "Mensaje eliminado correctamente"));
    }
}
