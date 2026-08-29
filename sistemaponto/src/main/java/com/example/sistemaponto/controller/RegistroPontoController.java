package com.example.sistemaponto.controller;

import com.example.sistemaponto.dto.AbrirPontoDTO;
import com.example.sistemaponto.dto.AjustarPontoDTO;
import com.example.sistemaponto.entity.RegistroPonto;
import com.example.sistemaponto.entity.Turma;
import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.enums.StatusPonto;
import com.example.sistemaponto.enums.TipoRegistro;
import com.example.sistemaponto.service.RegistroPontoService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registros")
public class RegistroPontoController {
  private final RegistroPontoService service;

  public RegistroPontoController(RegistroPontoService service) { this.service = service; }

  @PostMapping("/abrir")
  @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
  public RegistroPonto abrir(@Valid @RequestBody AbrirPontoDTO dto, Principal principal) {
    return service.abrir(principal.getName(), dto);
  }

  @PostMapping("/{id}/fechar")
  @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
  public RegistroPonto fechar(@PathVariable Long id, Principal principal) {
    return service.fechar(principal.getName(), id);
  }

  @GetMapping("/aberto")
  public RegistroPonto aberto(Principal principal) { return service.pontoAberto(principal.getName()); }

  @PutMapping("/{id}/ajustar")
  @PreAuthorize("hasRole('GESTOR')")
  public RegistroPonto ajustar(@PathVariable Long id, @Valid @RequestBody AjustarPontoDTO dto,
      Principal principal) {
    return service.ajustar(principal.getName(), id, dto);
  }

  @GetMapping
  public List<RegistroPonto> listar(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
      @RequestParam(required = false) Long usuarioId,
      @RequestParam(required = false) Long turmaId,
      @RequestParam(required = false) StatusPonto status,
      @RequestParam(required = false) TipoRegistro tipo,
      @RequestParam(required = false) String busca,
      Principal principal) {
    return service.filtrar(principal.getName(), inicio, fim, usuarioId, turmaId, status, tipo, busca);
  }

  @GetMapping("/{id}")
  public RegistroPonto buscar(@PathVariable Long id, Principal principal) {
    return service.buscar(id, principal.getName());
  }

  @GetMapping("/usuarios/visiveis")
  public List<Usuario> usuarios(Principal principal) {
    return service.usuariosVisiveis(principal.getName());
  }

  @GetMapping("/turmas-permitidas")
  public List<Turma> turmasPermitidas(Principal principal) {
    return service.turmasPermitidas(principal.getName());
  }
}
