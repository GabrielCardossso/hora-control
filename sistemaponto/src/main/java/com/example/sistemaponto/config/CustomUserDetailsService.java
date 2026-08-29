package com.example.sistemaponto.config;

import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UsuarioRepository repository;

  public CustomUserDetailsService(UsuarioRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Usuario usuario = repository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));
    return User.withUsername(usuario.getEmail())
        .password(usuario.getSenha())
        .roles(usuario.getTipoUsuario())
        .disabled(!usuario.isAtivo())
        .build();
  }
}
