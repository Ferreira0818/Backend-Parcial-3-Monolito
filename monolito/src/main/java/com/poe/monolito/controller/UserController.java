package com.poe.monolito.controller;

import com.poe.monolito.dto.AuthResponse;
import com.poe.monolito.dto.ChangePasswordRequest;
import com.poe.monolito.dto.UpdateProfileRequest;
import com.poe.monolito.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Panel Admin: listar todos los usuarios registrados activos
    @GetMapping
    public ResponseEntity<List<AuthResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AuthResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    // Pantalla 1: Guardar Cambios (Datos del Perfil)
    @PutMapping("/{userId}/profile")
    public ResponseEntity<AuthResponse> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    // Pantalla 1: Actualizar Contraseña (Seguridad & Contraseña)
    @PatchMapping("/{userId}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
    }

    // Panel Admin: eliminar (desactivar) un usuario no administrador
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
    }
}
