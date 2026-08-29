package com.example.sistemaponto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.sistemaponto.dto.AbrirPontoDTO;
import com.example.sistemaponto.dto.AjustarPontoDTO;
import com.example.sistemaponto.entity.Coordenador;
import com.example.sistemaponto.entity.Curso;
import com.example.sistemaponto.entity.Gestor;
import com.example.sistemaponto.entity.Professor;
import com.example.sistemaponto.entity.RegistroPonto;
import com.example.sistemaponto.entity.Turma;
import com.example.sistemaponto.enums.StatusPonto;
import com.example.sistemaponto.enums.TipoRegistro;
import com.example.sistemaponto.exception.RegraNegocioException;
import com.example.sistemaponto.repository.RegistroPontoRepository;
import com.example.sistemaponto.repository.TurmaRepository;
import com.example.sistemaponto.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistroPontoServiceTest {
  RegistroPontoRepository registros = mock(RegistroPontoRepository.class);
  UsuarioRepository usuarios = mock(UsuarioRepository.class);
  TurmaRepository turmas = mock(TurmaRepository.class);
  RegistroPontoService service = new RegistroPontoService(registros, usuarios, turmas);
  Professor professor;

  @BeforeEach void setup() {
    professor = new Professor();
    professor.setId(1L); professor.setNome("Professor"); professor.setEmail("p@teste.com"); professor.setAtivo(true);
    when(usuarios.findByEmailIgnoreCase("p@teste.com")).thenReturn(Optional.of(professor));
  }

  @Test void impedeSegundoPontoAberto() {
    RegistroPonto aberto = new RegistroPonto(); aberto.setEntrada(LocalDateTime.now());
    when(registros.findByUsuarioIdAndStatus(1L, StatusPonto.ABERTO)).thenReturn(Optional.of(aberto));
    AbrirPontoDTO dto = new AbrirPontoDTO(); dto.setTipo(TipoRegistro.ATIVIDADE_EXTRA); dto.setTurmaId(1L);
    assertThatThrownBy(() -> service.abrir("p@teste.com", dto))
        .isInstanceOf(RegraNegocioException.class).hasMessageContaining("ja possui");
  }

  @Test void professorAbrePontoSomenteEmTurmaVinculada() {
    Turma permitida = turma(2L, "PERMITIDA", true);
    professor.getTurmas().add(permitida);
    when(registros.findByUsuarioIdAndStatus(1L, StatusPonto.ABERTO)).thenReturn(Optional.empty());
    when(turmas.findById(2L)).thenReturn(Optional.of(permitida));
    when(registros.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
    AbrirPontoDTO dto = new AbrirPontoDTO(); dto.setTipo(TipoRegistro.AULA_NORMAL); dto.setTurmaId(2L);

    RegistroPonto salvo = service.abrir("p@teste.com", dto);

    assertThat(salvo.getUsuario()).isEqualTo(professor);
    assertThat(salvo.getTurma()).isEqualTo(permitida);
    assertThat(salvo.getStatus()).isEqualTo(StatusPonto.ABERTO);
  }

  @Test void professorNaoAbrePontoSemTurmaVinculada() {
    Turma bloqueada = turma(3L, "BLOQUEADA", true);
    when(registros.findByUsuarioIdAndStatus(1L, StatusPonto.ABERTO)).thenReturn(Optional.empty());
    when(turmas.findById(3L)).thenReturn(Optional.of(bloqueada));
    AbrirPontoDTO dto = new AbrirPontoDTO(); dto.setTipo(TipoRegistro.AULA_NORMAL); dto.setTurmaId(3L);

    assertThatThrownBy(() -> service.abrir("p@teste.com", dto)).hasMessageContaining("nao esta vinculada");
    verify(registros, never()).save(any());
  }

  @Test void gestorNaoRegistraPonto() {
    Gestor gestor = new Gestor(); gestor.setId(5L); gestor.setEmail("g@teste.com");
    when(usuarios.findByEmailIgnoreCase("g@teste.com")).thenReturn(Optional.of(gestor));
    AbrirPontoDTO dto = new AbrirPontoDTO(); dto.setTipo(TipoRegistro.AULA_NORMAL); dto.setTurmaId(2L);
    assertThatThrownBy(() -> service.abrir("g@teste.com", dto)).hasMessageContaining("nao registram ponto");
  }

  @Test void coordenadorVeProprioPontoEProfessoresDoCurso() {
    Curso curso = curso(10L); Curso outroCurso = curso(20L);
    Coordenador coordenador = new Coordenador(); coordenador.setId(3L); coordenador.setCursoResponsavel(curso);
    Professor outroProfessor = new Professor(); outroProfessor.setId(2L);
    RegistroPonto proprio = registro(coordenador, turma(30L, "COORD", true));
    proprio.getTurma().setCurso(outroCurso);
    RegistroPonto doCurso = registro(outroProfessor, turma(11L, "CURSO", true));
    doCurso.getTurma().setCurso(curso);
    RegistroPonto deOutroCurso = registro(outroProfessor, turma(21L, "OUTRO", true));
    deOutroCurso.getTurma().setCurso(outroCurso);
    when(registros.findAllByOrderByEntradaDesc()).thenReturn(List.of(proprio, doCurso, deOutroCurso));

    assertThat(service.visiveis(coordenador)).containsExactly(proprio, doCurso);
  }

  @Test void coordenadorNaoAjustaPontoDeProfessor() {
    Coordenador coordenador = new Coordenador(); coordenador.setId(3L); coordenador.setEmail("c@teste.com");
    coordenador.setCursoResponsavel(curso(10L));
    when(usuarios.findByEmailIgnoreCase("c@teste.com")).thenReturn(Optional.of(coordenador));
    AjustarPontoDTO dto = ajuste();
    assertThatThrownBy(() -> service.ajustar("c@teste.com", 10L, dto))
        .hasMessageContaining("Somente o gestor");
    verify(registros, never()).save(any());
  }

  @Test void gestorAjustaComTrilhaDeAuditoria() {
    Gestor gestor = new Gestor(); gestor.setId(5L); gestor.setEmail("g@teste.com");
    when(usuarios.findByEmailIgnoreCase("g@teste.com")).thenReturn(Optional.of(gestor));
    RegistroPonto registro = registro(professor, turma(2L, "T", true)); registro.setId(10L);
    when(registros.findById(10L)).thenReturn(Optional.of(registro));
    when(registros.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

    RegistroPonto ajustado = service.ajustar("g@teste.com", 10L, ajuste());

    assertThat(ajustado.isAjustado()).isTrue();
    assertThat(ajustado.getAlteradoPor()).isEqualTo(gestor);
    assertThat(ajustado.getJustificativaAjuste()).isEqualTo("Correcao autorizada");
  }

  @Test void relatorioDoCoordenadorContemSomentePropriosRegistros() {
    Curso curso = curso(10L);
    Coordenador coordenador = new Coordenador(); coordenador.setId(3L); coordenador.setEmail("c@teste.com");
    coordenador.setCursoResponsavel(curso);
    Professor outro = new Professor(); outro.setId(4L);
    RegistroPonto proprio = registro(coordenador, turma(11L, "T1", true));
    RegistroPonto alheio = registro(outro, turma(12L, "T2", true));
    when(usuarios.findByEmailIgnoreCase("c@teste.com")).thenReturn(Optional.of(coordenador));
    when(registros.findByUsuarioIdOrderByEntradaDesc(3L)).thenReturn(List.of(proprio));

    assertThat(service.filtrarRelatorio("c@teste.com", null, null, 4L, null, null, null, null))
        .containsExactly(proprio);
    assertThat(service.visiveis(coordenador)).doesNotContain(alheio);
  }

  private Curso curso(Long id) { Curso curso = new Curso(); curso.setId(id); return curso; }
  private Turma turma(Long id, String codigo, boolean ativa) {
    Turma turma = new Turma(); turma.setId(id); turma.setCodigo(codigo); turma.setAtivo(ativa); return turma;
  }
  private RegistroPonto registro(com.example.sistemaponto.entity.Usuario usuario, Turma turma) {
    RegistroPonto registro = new RegistroPonto(); registro.setUsuario(usuario); registro.setTurma(turma);
    registro.setEntrada(LocalDateTime.now()); registro.setStatus(StatusPonto.FECHADO); return registro;
  }
  private AjustarPontoDTO ajuste() {
    AjustarPontoDTO dto = new AjustarPontoDTO();
    dto.setEntrada(LocalDateTime.of(2026, 8, 10, 8, 0));
    dto.setSaida(LocalDateTime.of(2026, 8, 10, 10, 0));
    dto.setJustificativa("Correcao autorizada");
    return dto;
  }
}
