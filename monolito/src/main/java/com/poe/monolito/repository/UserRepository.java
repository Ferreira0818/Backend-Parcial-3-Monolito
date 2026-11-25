package com.poe.monolito.repository;

import com.poe.monolito.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Listar usuarios activos ordenados por ID para el panel de administración
    List<User> findAllByActiveTrueOrderByIdAsc();

    // Buscar usuario activo por ID
    Optional<User> findByIdAndActiveTrue(Long id);

    // Buscar usuario activo por correo para login o reseteo de contraseña
    Optional<User> findByEmailAndActiveTrue(String email);

    // Comprobar existencia rápida por correo (incluye inactivos para respetar UNIQUE en BD)
    boolean existsByEmail(String email);
}