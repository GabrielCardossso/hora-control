package com.example.sistemaponto.config;

import com.example.sistemaponto.entity.Gestor;
import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final String gestorEmail;
  private final String gestorPassword;

  public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
      @Value("${horacontrol.gestor.email}") String gestorEmail,
      @Value("${horacontrol.gestor.password}") String gestorPassword) {
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.gestorEmail = gestorEmail;
    this.gestorPassword = gestorPassword;
  }

  @Override
  public void run(String... args) {
    Usuario usuario = usuarioRepository.findByEmailIgnoreCase(gestorEmail).orElse(null);
    if (usuario == null) {
      if (gestorPassword == null || gestorPassword.length() < 6)
        throw new IllegalStateException(
            "Defina GESTOR_PASSWORD com pelo menos 6 caracteres para criar o gestor inicial.");
      Gestor gestor = new Gestor();
      gestor.setNome("Gestor educacional");
      gestor.setEmail(gestorEmail.toLowerCase());
      gestor.setSenha(passwordEncoder.encode(gestorPassword));
      gestor.setAtivo(true);
      usuarioRepository.save(gestor);
      return;
    }
    if (!(usuario instanceof Gestor))
      throw new IllegalStateException("O e-mail administrativo deve pertencer a um Gestor.");
    usuario.setAtivo(true);
    usuarioRepository.save(usuario);
  }
}
