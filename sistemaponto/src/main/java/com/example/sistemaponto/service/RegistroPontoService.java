package com.example.sistemaponto.service;

import com.example.sistemaponto.dto.AbrirPontoDTO;
import com.example.sistemaponto.dto.AjustarPontoDTO;
import com.example.sistemaponto.entity.Coordenador;
import com.example.sistemaponto.entity.Gestor;
import com.example.sistemaponto.entity.Professor;
import com.example.sistemaponto.entity.RegistroPonto;
import com.example.sistemaponto.entity.Turma;
import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.enums.StatusPonto;
import com.example.sistemaponto.enums.TipoRegistro;
import com.example.sistemaponto.exception.RegraNegocioException;
import com.example.sistemaponto.repository.RegistroPontoRepository;
import com.example.sistemaponto.repository.TurmaRepository;
import com.example.sistemaponto.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroPontoService {
  private final RegistroPontoRepository registroRepository;
  private final UsuarioRepository usuarioRepository;
  private final TurmaRepository turmaRepository;

  public RegistroPontoService(RegistroPontoRepository registroRepository,
      UsuarioRepository usuarioRepository, TurmaRepository turmaRepository) {
    this.registroRepository = registroRepository;
    this.usuarioRepository = usuarioRepository;
    this.turmaRepository = turmaRepository;
  }

  @Transactional
  public RegistroPonto abrir(String email, AbrirPontoDTO dto) {
    Usuario usuario = usuario(email);
    if (!usuario.podeRegistrarPonto())
      throw new RegraNegocioException("Gestores educacionais nao registram ponto.");
    registroRepository.findByUsuarioIdAndStatus(usuario.getId(), StatusPonto.ABERTO)
        .ifPresent(r -> { throw new RegraNegocioException("Voce ja possui um ponto aberto desde " + r.getEntrada() + "."); });

    if (dto.getTurmaId() == null)
      throw new RegraNegocioException("Selecione uma turma vinculada ao seu cadastro.");
    Turma turma = turmaRepository.findById(dto.getTurmaId())
        .orElseThrow(() -> new RegraNegocioException("Turma nao encontrada."));
    if (!turma.isAtivo())
      throw new RegraNegocioException("Esta turma esta inativa.");
    if (turmasDoUsuario(usuario).stream().noneMatch(t -> t.getId().equals(turma.getId())))
      throw new RegraNegocioException("Esta turma nao esta vinculada ao seu cadastro.");

    RegistroPonto registro = new RegistroPonto();
    registro.setUsuario(usuario);
    registro.setTurma(turma);
    registro.setTipo(dto.getTipo());
    registro.setDescricao(limpar(dto.getDescricao()));
    registro.setObservacao(limpar(dto.getObservacao()));
    registro.setEntrada(LocalDateTime.now());
    registro.setStatus(StatusPonto.ABERTO);
    return registroRepository.save(registro);
  }

  @Transactional
  public RegistroPonto fechar(String email, Long id) {
    Usuario solicitante = usuario(email);
    RegistroPonto registro = buscarObrigatorio(id);
    if (!solicitante.getId().equals(registro.getUsuario().getId()))
      throw new RegraNegocioException("Voce nao pode fechar o ponto de outro usuario.");
    if (!solicitante.podeRegistrarPonto())
      throw new RegraNegocioException("Gestores educacionais nao registram ponto.");
    if (registro.getStatus() == StatusPonto.FECHADO)
      throw new RegraNegocioException("Este ponto ja esta fechado.");
    registro.setSaida(LocalDateTime.now());
    registro.setStatus(StatusPonto.FECHADO);
    return registroRepository.save(registro);
  }

  public RegistroPonto pontoAberto(String email) {
    Usuario usuario = usuario(email);
    if (!usuario.podeRegistrarPonto()) return null;
    return registroRepository.findByUsuarioIdAndStatus(usuario.getId(), StatusPonto.ABERTO).orElse(null);
  }

  @Transactional
  public RegistroPonto ajustar(String email, Long id, AjustarPontoDTO dto) {
    Usuario solicitante = usuario(email);
    if (!(solicitante instanceof Gestor gestor))
      throw new RegraNegocioException("Somente o gestor educacional pode ajustar registros.");
    RegistroPonto registro = buscarObrigatorio(id);
    if (!dto.getSaida().isAfter(dto.getEntrada()))
      throw new RegraNegocioException("A saida deve ser posterior a entrada.");
    registro.setEntrada(dto.getEntrada());
    registro.setSaida(dto.getSaida());
    registro.setStatus(StatusPonto.FECHADO);
    registro.setObservacao(limpar(dto.getObservacao()));
    registro.setAjustado(true);
    registro.setJustificativaAjuste(dto.getJustificativa().trim());
    registro.setAlteradoPor(gestor);
    registro.setDataAlteracao(LocalDateTime.now());
    return registroRepository.save(registro);
  }

  public List<RegistroPonto> filtrar(String email, LocalDate inicio, LocalDate fim, Long usuarioId,
      Long turmaId, StatusPonto status, TipoRegistro tipo, String busca) {
    Usuario solicitante = usuario(email);
    return aplicarFiltros(visiveis(solicitante), inicio, fim, usuarioId, turmaId, status, tipo, busca);
  }

  public List<RegistroPonto> filtrarRelatorio(String email, LocalDate inicio, LocalDate fim, Long usuarioId,
      Long turmaId, StatusPonto status, TipoRegistro tipo, String busca) {
    Usuario solicitante = usuario(email);
    List<RegistroPonto> base = solicitante instanceof Gestor
        ? registroRepository.findAllByOrderByEntradaDesc()
        : registroRepository.findByUsuarioIdOrderByEntradaDesc(solicitante.getId());
    Long filtroUsuario = solicitante instanceof Gestor ? usuarioId : solicitante.getId();
    return aplicarFiltros(base, inicio, fim, filtroUsuario, turmaId, status, tipo, busca);
  }

  private List<RegistroPonto> aplicarFiltros(List<RegistroPonto> base, LocalDate inicio, LocalDate fim,
      Long usuarioId, Long turmaId, StatusPonto status, TipoRegistro tipo, String busca) {
    Stream<RegistroPonto> fluxo = base.stream();
    if (inicio != null) fluxo = fluxo.filter(r -> !r.getEntrada().toLocalDate().isBefore(inicio));
    if (fim != null) fluxo = fluxo.filter(r -> !r.getEntrada().toLocalDate().isAfter(fim));
    if (usuarioId != null) fluxo = fluxo.filter(r -> r.getUsuario().getId().equals(usuarioId));
    if (turmaId != null) fluxo = fluxo.filter(r -> r.getTurma() != null && r.getTurma().getId().equals(turmaId));
    if (status != null) fluxo = fluxo.filter(r -> r.getStatus() == status);
    if (tipo != null) fluxo = fluxo.filter(r -> r.getTipo() == tipo);
    if (busca != null && !busca.isBlank()) {
      String termo = busca.toLowerCase(Locale.ROOT);
      fluxo = fluxo.filter(r -> contem(r.getDescricao(), termo) || contem(r.getObservacao(), termo)
          || (r.getTurma() != null && (contem(r.getTurma().getNome(), termo)
              || contem(r.getTurma().getCodigo(), termo)))
          || contem(r.getUsuario().getNome(), termo));
    }
    return fluxo.toList();
  }

  public List<RegistroPonto> visiveis(Usuario solicitante) {
    if (solicitante instanceof Gestor)
      return registroRepository.findAllByOrderByEntradaDesc();
    if (solicitante instanceof Coordenador coordenador) {
      Long cursoId = coordenador.getCursoResponsavel().getId();
      return registroRepository.findAllByOrderByEntradaDesc().stream()
          .filter(r -> r.getUsuario().getId().equals(solicitante.getId())
              || (r.getUsuario() instanceof Professor && pertenceAoCurso(r, cursoId)))
          .toList();
    }
    return registroRepository.findByUsuarioIdOrderByEntradaDesc(solicitante.getId());
  }

  public Usuario usuario(String email) {
    return usuarioRepository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new RegraNegocioException("Usuario autenticado nao encontrado."));
  }

  public List<Usuario> usuariosVisiveis(String email) {
    Usuario solicitante = usuario(email);
    if (solicitante instanceof Gestor)
      return usuarioRepository.findAllByOrderByNomeAsc().stream().filter(Usuario::isAtivo).toList();
    if (solicitante instanceof Coordenador coordenador) {
      Long cursoId = coordenador.getCursoResponsavel().getId();
      LinkedHashMap<Long, Usuario> mapa = new LinkedHashMap<>();
      mapa.put(solicitante.getId(), solicitante);
      usuarioRepository.findAllByOrderByNomeAsc().stream()
          .filter(Professor.class::isInstance).map(Professor.class::cast)
          .filter(Usuario::isAtivo)
          .filter(p -> p.getTurmas().stream().anyMatch(t -> pertenceAoCurso(t, cursoId)))
          .forEach(p -> mapa.put(p.getId(), p));
      return mapa.values().stream().sorted(Comparator.comparing(Usuario::getNome)).toList();
    }
    return List.of(solicitante);
  }

  @Transactional(readOnly = true)
  public List<Turma> turmasPermitidas(String email) {
    Usuario solicitante = usuario(email);
    return turmasDoUsuario(solicitante).stream().filter(Turma::isAtivo)
        .sorted(Comparator.comparing(Turma::getCodigo, String.CASE_INSENSITIVE_ORDER)).toList();
  }

  public RegistroPonto buscar(Long id, String email) {
    Usuario solicitante = usuario(email);
    RegistroPonto registro = buscarObrigatorio(id);
    if (!podeVisualizar(solicitante, registro))
      throw new RegraNegocioException("Acesso negado a este registro.");
    return registro;
  }

  private RegistroPonto buscarObrigatorio(Long id) {
    return registroRepository.findById(id)
        .orElseThrow(() -> new RegraNegocioException("Registro nao encontrado."));
  }

  private boolean podeVisualizar(Usuario usuario, RegistroPonto registro) {
    if (usuario instanceof Gestor || usuario.getId().equals(registro.getUsuario().getId())) return true;
    return usuario instanceof Coordenador coordenador
        && registro.getUsuario() instanceof Professor
        && pertenceAoCurso(registro, coordenador.getCursoResponsavel().getId());
  }

  private Set<Turma> turmasDoUsuario(Usuario usuario) {
    if (usuario instanceof Professor professor) return professor.getTurmas();
    if (usuario instanceof Coordenador coordenador) return coordenador.getTurmas();
    return Set.of();
  }

  private boolean pertenceAoCurso(RegistroPonto registro, Long cursoId) {
    return registro.getTurma() != null && pertenceAoCurso(registro.getTurma(), cursoId);
  }

  private boolean pertenceAoCurso(Turma turma, Long cursoId) {
    return turma.getCurso() != null && turma.getCurso().getId().equals(cursoId);
  }

  private boolean contem(String valor, String termo) {
    return valor != null && valor.toLowerCase(Locale.ROOT).contains(termo);
  }

  private String limpar(String valor) {
    return valor == null || valor.isBlank() ? null : valor.trim();
  }
}
