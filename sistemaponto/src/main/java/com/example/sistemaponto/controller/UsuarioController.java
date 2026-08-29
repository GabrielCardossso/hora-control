package com.example.sistemaponto.controller;

import com.example.sistemaponto.dto.UsuarioRequestDTO;
import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('GESTOR')")
public class UsuarioController {
  private final UsuarioService service;

  public UsuarioController(UsuarioService service) { this.service = service; }

  @GetMapping
  public List<Usuario> listar() { return service.listar(); }

  @GetMapping("/{id}")
  public Usuario buscar(@PathVariable Long id) { return service.buscar(id); }

  @PostMapping
  public Usuario salvar(@Valid @RequestBody UsuarioRequestDTO dto) { return service.criar(dto); }

  @PutMapping("/{id}")
  public Usuario atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
    return service.atualizar(id, dto);
  }

  @DeleteMapping("/{id}")
  public void excluir(@PathVariable Long id) { service.excluir(id); }
}
