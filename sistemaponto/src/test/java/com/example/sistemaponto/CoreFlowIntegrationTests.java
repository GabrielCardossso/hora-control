package com.example.sistemaponto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.sistemaponto.entity.Coordenador;
import com.example.sistemaponto.entity.Curso;
import com.example.sistemaponto.entity.Professor;
import com.example.sistemaponto.entity.Turma;
import com.example.sistemaponto.entity.Usuario;
import com.example.sistemaponto.repository.CursoRepository;
import com.example.sistemaponto.repository.TurmaRepository;
import com.example.sistemaponto.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CoreFlowIntegrationTests {
  @Autowired MockMvc mvc;
  @Autowired UsuarioRepository usuarios;
  @Autowired TurmaRepository turmas;
  @Autowired CursoRepository cursos;
  @Autowired PasswordEncoder encoder;
  @Autowired ObjectMapper mapper;

  @Test void professorAbreEFechaPontoSomenteNaTurmaVinculada() throws Exception {
    Professor professor = new Professor();
    professor.setNome("Professor Integracao");
    professor.setEmail("integracao@teste.local");
    professor.setSenha(encoder.encode("123456"));
    professor.setAtivo(true);
    professor = usuarios.save(professor);
    Turma turma = new Turma();
    turma.setCodigo("INT-01"); turma.setNome("Turma Integracao"); turma.setAtivo(true);
    turma = turmas.save(turma);
    professor.getTurmas().add(turma);
    professor = usuarios.save(professor);

    mvc.perform(get("/api/registros/turmas-permitidas")
        .with(user(professor.getEmail()).roles("PROFESSOR")))
        .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));

    String resposta = mvc.perform(post("/api/registros/abrir")
        .with(user(professor.getEmail()).roles("PROFESSOR")).with(csrf())
        .contentType("application/json")
        .content("{\"tipo\":\"AULA_NORMAL\",\"turmaId\":" + turma.getId() + "}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ABERTO"))
        .andExpect(jsonPath("$.usuario.tipoUsuario").value("PROFESSOR"))
        .andReturn().getResponse().getContentAsString();
    JsonNode json = mapper.readTree(resposta);
    mvc.perform(post("/api/registros/" + json.get("id").asLong() + "/fechar")
        .with(user(professor.getEmail()).roles("PROFESSOR")).with(csrf()))
        .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("FECHADO"));
  }

  @Test void gestorCriaProfessorComSenhaProtegida() throws Exception {
    mvc.perform(post("/api/admin/usuarios").with(user("admin@teste.local").roles("GESTOR")).with(csrf())
        .contentType("application/json")
        .content("{\"nome\":\"Nova Professora\",\"email\":\"nova@teste.local\",\"tipoUsuario\":\"PROFESSOR\",\"senha\":\"segura123\",\"ativo\":true}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.tipoUsuario").value("PROFESSOR"))
        .andExpect(jsonPath("$.senha").doesNotExist());
    Usuario salvo = usuarios.findByEmailIgnoreCase("nova@teste.local").orElseThrow();
    assertThat(salvo).isInstanceOf(Professor.class);
    assertThat(encoder.matches("segura123", salvo.getSenha())).isTrue();
  }

  @Test void coordenadorExigeCursoResponsavel() throws Exception {
    mvc.perform(post("/api/admin/usuarios").with(user("admin@teste.local").roles("GESTOR")).with(csrf())
        .contentType("application/json")
        .content("{\"nome\":\"Coordenador\",\"email\":\"coord@teste.local\",\"tipoUsuario\":\"COORDENADOR\",\"senha\":\"segura123\",\"ativo\":true}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.mensagem").value("Selecione o curso sob responsabilidade do coordenador."));
  }

  @Test void criaCoordenadorComoSubclasseComCurso() throws Exception {
    Curso curso = new Curso(); curso.setCodigo("TDS"); curso.setNome("Desenvolvimento"); curso.setAtivo(true);
    curso = cursos.save(curso);
    mvc.perform(post("/api/admin/usuarios").with(user("admin@teste.local").roles("GESTOR")).with(csrf())
        .contentType("application/json")
        .content("{\"nome\":\"Coordenador\",\"email\":\"coord2@teste.local\",\"tipoUsuario\":\"COORDENADOR\",\"senha\":\"segura123\",\"ativo\":true,\"cursoResponsavelId\":" + curso.getId() + "}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.tipoUsuario").value("COORDENADOR"));
    assertThat(usuarios.findByEmailIgnoreCase("coord2@teste.local").orElseThrow()).isInstanceOf(Coordenador.class);
  }

  @Test void gestorEditaCurso() throws Exception {
    Curso curso = new Curso(); curso.setCodigo("TDS-OLD"); curso.setNome("Nome antigo"); curso.setAtivo(true);
    curso = cursos.save(curso);
    mvc.perform(put("/api/cursos/" + curso.getId()).with(user("admin@teste.local").roles("GESTOR")).with(csrf())
        .contentType("application/json")
        .content("{\"codigo\":\"TDS-NOVO\",\"nome\":\"Nome corrigido\",\"ativo\":true}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Nome corrigido"));
  }
}
