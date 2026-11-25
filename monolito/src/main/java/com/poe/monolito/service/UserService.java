package com.poe.monolito.service;

import com.poe.monolito.dto.*;
import com.poe.monolito.entity.Cart;
import com.poe.monolito.entity.User;
import com.poe.monolito.entity.enums.Role;
import com.poe.monolito.repository.CartRepository;
import com.poe.monolito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository; // Vincularemos el carrito al registrar
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El correo ya está registrado en Wraeclast");
        }

        // 1. Crear el usuario con contraseña hasheada
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        // 2. Crear y asociar su carrito de compras persistente inicial
        Cart cart = Cart.builder()
                .user(savedUser)
                .build();
        cartRepository.save(cart);

        return mapToAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndActiveTrue(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        // Comprobamos la contraseña ingresada contra el hash BCrypt de la BD
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return mapToAuthResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar contraseña actual con BCrypt
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            // Este mensaje exacto alimenta la caja roja de tu interfaz en React
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }

        // Cifrar y guardar la nueva contraseña
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setName(request.name());
        User updated = userRepository.save(user);

        return mapToAuthResponse(updated);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmailAndActiveTrue(request.email())
                .orElseThrow(() -> new IllegalArgumentException("No existe ninguna cuenta asociada a este correo"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse getProfile(Long userId) {
        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return mapToAuthResponse(user);
    }

    // Listado completo de usuarios activos para el panel Admin
    @Transactional(readOnly = true)
    public List<AuthResponse> getAllUsers() {
        return userRepository.findAllByActiveTrueOrderByIdAsc()
                .stream()
                .map(this::mapToAuthResponse)
                .toList();
    }

    // Soft Delete de usuario no administrador y limpieza de su carrito
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("No se puede eliminar un usuario administrador");
        }

        user.setActive(false);
        userRepository.save(user);

        cartRepository.findByUserId(userId).ifPresent(cartRepository::delete);
    }

    private AuthResponse mapToAuthResponse(User user) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
