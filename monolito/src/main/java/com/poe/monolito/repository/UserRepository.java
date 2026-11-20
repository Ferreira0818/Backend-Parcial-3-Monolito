package com.poe.monolito.repository;

import com.poe.monolito.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por correo para login o reseteo de contraseña
    Optional<User> findByEmail(String email);

    // Comprobar existencia rápida al registrar
    boolean existsByEmail(String email);
}