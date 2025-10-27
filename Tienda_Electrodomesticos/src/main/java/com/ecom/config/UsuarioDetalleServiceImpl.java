package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecom.model.Usuario;
import com.ecom.repository.UsuarioRepository;
@Service
public class UsuarioDetalleServiceImpl implements UserDetailsService{
    @Autowired
    private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Usuario usuario =  usuarioRepository.findByEmail(username);
        if(usuario == null){
            throw new UsernameNotFoundException("Usuario no encontrado");

        }
        return new UsuarioPersonalizado(usuario);
	}
    

}
