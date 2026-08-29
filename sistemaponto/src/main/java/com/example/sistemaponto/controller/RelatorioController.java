package com.example.sistemaponto.controller;

import com.example.sistemaponto.entity.Gestor;
import com.example.sistemaponto.entity.RegistroPonto;
import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.enums.StatusPonto;
import com.example.sistemaponto.enums.TipoRegistro;
import com.example.sistemaponto.service.RegistroPontoService;
import com.example.sistemaponto.service.RelatorioService;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
  private final RegistroPontoService registros;
  private final RelatorioService relatorios;

  public RelatorioController(RegistroPontoService registros, RelatorioService relatorios) {
    this.registros = registros;
    this.relatorios = relatorios;
  }

  @GetMapping("/pdf")
  public ResponseEntity<byte[]> gerarPdf(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
      @RequestParam(required = false) Long usuarioId,
      @RequestParam(required = false) Long turmaId,
      @RequestParam(required = false) StatusPonto status,
      @RequestParam(required = false) TipoRegistro tipo,
      @RequestParam(required = false) String busca,
      Principal principal) {
    Usuario solicitante = registros.usuario(principal.getName());
    List<RegistroPonto> lista = registros.filtrarRelatorio(principal.getName(), inicio, fim, usuarioId,
        turmaId, status, tipo, busca);
    String escopo = solicitante instanceof Gestor ? "Relatorio global de ponto" : "Meu relatorio de ponto";
    String data = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"relatorio-ponto-" + data + ".pdf\"")
        .body(relatorios.gerarPdf(lista, "HoraControl - " + escopo));
  }

  @GetMapping("/registros")
  public List<RegistroPonto> visualizar(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
      @RequestParam(required = false) Long usuarioId,
      @RequestParam(required = false) Long turmaId,
      @RequestParam(required = false) StatusPonto status,
      @RequestParam(required = false) TipoRegistro tipo,
      @RequestParam(required = false) String busca,
      Principal principal) {
    return registros.filtrarRelatorio(principal.getName(), inicio, fim, usuarioId, turmaId, status, tipo, busca);
  }

  @GetMapping("/usuarios")
  public List<Usuario> usuarios(Principal principal) {
    Usuario solicitante = registros.usuario(principal.getName());
    return solicitante instanceof Gestor ? registros.usuariosVisiveis(principal.getName()) : List.of(solicitante);
  }
}
