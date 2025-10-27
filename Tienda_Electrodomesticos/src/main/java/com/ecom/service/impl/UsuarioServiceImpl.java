package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.Usuario;
import com.ecom.repository.UsuarioRepository;
import com.ecom.service.UsuarioService;
import com.ecom.util.AppConstant;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("ROLE_USER");
        }
        usuario.setIsEnable(true);
        usuario.setCuentaNoBloqueada(true);
        usuario.setIntentoFallido(0);

        // 🔹 Solo encriptar si no está ya encriptada
        if (!usuario.getPassword().startsWith("$2a$")) { // prefijo típico de BCrypt
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public Usuario getUserByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> getUsuarios(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {

        Optional<Usuario> findByUsuaro = usuarioRepository.findById(id);

        if (findByUsuaro.isPresent()) {
            Usuario usuario = findByUsuaro.get();
            usuario.setIsEnable(status);
            usuarioRepository.save(usuario);
        }

        return false;
    }

    @Override
    public void incrementaContadorIntentoFallido(Usuario usuario) {
        int attempt = usuario.getIntentoFallido() + 1;
        usuario.setIntentoFallido(attempt);
        usuarioRepository.save(usuario);
    }

    @Override
    public void cuentaUsuarioBloqueado(Usuario usuario) {
        usuario.setCuentaNoBloqueada(false);
        usuario.setLockTime(new Date());
        usuarioRepository.save(usuario);
    }

    @Override
    public boolean desbloquearCuentaTiempoExpirado(Usuario usuario) {
        if (usuario.getLockTime() == null) {
            return false; // No hay tiempo de bloqueo registrado
        }

        long lockTime = usuario.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
        long currentTime = System.currentTimeMillis();

        if (unlockTime < currentTime) {
            usuario.setCuentaNoBloqueada(true);
            usuario.setIntentoFallido(0);
            usuario.setLockTime(null);
            usuarioRepository.save(usuario);
            return true;
        }

        return false;
    }

    @Override
    public void reiniciaContador(int usuarioId) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        Usuario findByEmail = usuarioRepository.findByEmail(email);
        findByEmail.setResetToken(resetToken);
        usuarioRepository.save(findByEmail);
    }

    @Override
    public Usuario getUserByToken(String token) {
       return usuarioRepository.findByResetToken(token);
      
    }

    @Override
    public Usuario updateUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    
}   
