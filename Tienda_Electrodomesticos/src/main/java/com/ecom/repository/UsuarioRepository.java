package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    public Usuario findByEmail(String email);

    boolean existsByEmail(String email);

    public List<Usuario> findByRol(String rol);

    public Usuario findByResetToken(String token);
}
