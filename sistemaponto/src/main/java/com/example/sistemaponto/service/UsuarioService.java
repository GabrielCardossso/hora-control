package com.example.sistemaponto.service;

import com.example.sistemaponto.dto.AlterarSenhaDTO;
import com.example.sistemaponto.dto.UsuarioRequestDTO;
import com.example.sistemaponto.entity.Coordenador;
import com.example.sistemaponto.entity.Gestor;
import com.example.sistemaponto.entity.Professor;
import com.example.sistemaponto.entity.Turma;
import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.exception.RegraNegocioException;
import com.example.sistemaponto.repository.CursoRepository;
import com.example.sistemaponto.repository.TurmaRepository;
import com.example.sistemaponto.repository.UsuarioRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
  private final UsuarioRepository repository;
  private final CursoRepository cursoRepository;
  private final TurmaRepository turmaRepository;
  private final PasswordEncoder passwordEncoder;

  public UsuarioService(UsuarioRepository repository, CursoRepository cursoRepository,
      TurmaRepository turmaRepository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.cursoRepository = cursoRepository;
    this.turmaRepository = turmaRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<Usuario> listar() { return repository.findAllByOrderByNomeAsc(); }

  public Usuario buscar(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado."));
  }

  public Usuario buscarPorEmail(String email) {
    return repository.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado."));
  }

  @Transactional
  public Usuario criar(UsuarioRequestDTO dto) {
    if (repository.existsByEmailIgnoreCase(dto.getEmail()))
      throw new RegraNegocioException("Ja existe uma conta com este e-mail.");
    if (dto.getSenha() == null || dto.getSenha().length() < 6)
      throw new RegraNegocioException("Informe uma senha inicial com pelo menos 6 caracteres.");
    Usuario usuario = novoUsuario(dto.getTipoUsuario());
    aplicar(usuario, dto);
    usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
    return repository.save(usuario);
  }

  @Transactional
  public Usuario atualizar(Long id, UsuarioRequestDTO dto) {
    Usuario usuario = buscar(id);
    if (!usuario.getTipoUsuario().equals(normalizarTipo(dto.getTipoUsuario())))
      throw new RegraNegocioException(
          "O tipo de um usuario existente nao pode ser alterado. Desative a conta e crie outra com o novo tipo.");
    if (repository.existsByEmailIgnoreCaseAndIdNot(dto.getEmail(), id))
      throw new RegraNegocioException("Ja existe outra conta com este e-mail.");
    aplicar(usuario, dto);
    if (dto.getSenha() != null && !dto.getSenha().isBlank())
      usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
    return repository.save(usuario);
  }

  private Usuario novoUsuario(String tipo) {
    return switch (normalizarTipo(tipo)) {
      case "PROFESSOR" -> new Professor();
      case "COORDENADOR" -> new Coordenador();
      case "GESTOR" -> new Gestor();
      default -> throw new RegraNegocioException("Tipo de usuario invalido.");
    };
  }

  private String normalizarTipo(String tipo) {
    return tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT);
  }

  private void aplicar(Usuario usuario, UsuarioRequestDTO dto) {
    usuario.setNome(dto.getNome().trim());
    usuario.setMatricula(limpar(dto.getMatricula()));
    usuario.setCpf(limpar(dto.getCpf()));
    usuario.setEmail(dto.getEmail().trim().toLowerCase(Locale.ROOT));
    usuario.setTelefone(limpar(dto.getTelefone()));
    usuario.setAtivo(dto.isAtivo());

    LinkedHashSet<Turma> turmas = carregarTurmas(dto);
    if (usuario instanceof Professor professor) {
      professor.setTurmas(turmas);
    } else if (usuario instanceof Coordenador coordenador) {
      if (dto.getCursoResponsavelId() == null)
        throw new RegraNegocioException("Selecione o curso sob responsabilidade do coordenador.");
      coordenador.setCursoResponsavel(cursoRepository.findById(dto.getCursoResponsavelId())
          .orElseThrow(() -> new RegraNegocioException("Curso nao encontrado.")));
      coordenador.setTurmas(turmas);
    } else if (!turmas.isEmpty() || dto.getCursoResponsavelId() != null) {
      throw new RegraNegocioException("Gestores nao podem ser vinculados a turmas ou cursos para registro de ponto.");
    }
  }

  private LinkedHashSet<Turma> carregarTurmas(UsuarioRequestDTO dto) {
    LinkedHashSet<Turma> turmas = new LinkedHashSet<>();
    if (dto.getTurmaIds() != null) dto.getTurmaIds().forEach(id -> turmas.add(
        turmaRepository.findById(id)
            .orElseThrow(() -> new RegraNegocioException("Turma nao encontrada: " + id))));
    return turmas;
  }

  @Transactional
  public void alterarSenha(String email, AlterarSenhaDTO dto) {
    Usuario usuario = buscarPorEmail(email);
    if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha()))
      throw new RegraNegocioException("A senha atual esta incorreta.");
    usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
    repository.save(usuario);
  }

  @Transactional
  public void excluir(Long id) {
    Usuario usuario = buscar(id);
    usuario.setAtivo(false);
    repository.save(usuario);
  }

  private String limpar(String valor) {
    return valor == null || valor.isBlank() ? null : valor.trim();
  }
}
