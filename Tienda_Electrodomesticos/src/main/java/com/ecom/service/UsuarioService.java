package com.ecom.service;

import java.util.List;

import com.ecom.model.Usuario;

public interface UsuarioService {

    public Usuario saveUsuario(Usuario usuario);

    public Usuario getUserByEmail(String email);

    boolean existsByEmail(String email);

    public List<Usuario> getUsuarios(String rol);

    public Boolean updateAccountStatus(Integer id, Boolean status);

    public void incrementaContadorIntentoFallido(Usuario usuario);

    public void cuentaUsuarioBloqueado(Usuario usuario);

    public boolean desbloquearCuentaTiempoExpirado(Usuario usuario);

    public void reiniciaContador(int usuarioId);

    public void updateUserResetToken(String email, String resetToken);

    public Usuario getUserByToken(String token);

    public Usuario updateUser(Usuario usuario);
}
