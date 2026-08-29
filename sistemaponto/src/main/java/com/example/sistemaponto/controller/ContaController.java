package com.example.sistemaponto.controller;

import com.example.sistemaponto.dto.AlterarSenhaDTO;
import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.service.UsuarioService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conta")
public class ContaController {
  private final UsuarioService service;

  public ContaController(UsuarioService service) { this.service = service; }

  @GetMapping
  public Usuario atual(Principal principal) { return service.buscarPorEmail(principal.getName()); }

  @PostMapping("/senha")
  public Map<String, String> senha(@Valid @RequestBody AlterarSenhaDTO dto, Principal principal) {
    service.alterarSenha(principal.getName(), dto);
    return Map.of("mensagem", "Senha alterada com sucesso.");
  }
}
