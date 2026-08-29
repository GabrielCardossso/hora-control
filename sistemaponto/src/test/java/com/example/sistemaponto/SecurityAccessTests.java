package com.example.sistemaponto;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAccessTests {
  @Autowired MockMvc mvc;

  @Test void redirecionaAnonimoParaLogin() throws Exception {
    mvc.perform(get("/perfil")).andExpect(status().is3xxRedirection());
  }

  @Test @WithMockUser(roles = "PROFESSOR")
  void professorNaoAcessaGestao() throws Exception {
    mvc.perform(get("/usuarios")).andExpect(status().isForbidden());
    mvc.perform(get("/gestao-turmas")).andExpect(status().isForbidden());
  }

  @Test @WithMockUser(roles = "COORDENADOR")
  void coordenadorNaoAcessaGestaoMasAcessaRelatorioProprio() throws Exception {
    mvc.perform(get("/usuarios")).andExpect(status().isForbidden());
    mvc.perform(get("/relatorios")).andExpect(status().isOk()).andExpect(view().name("Relatorios"));
  }

  @Test @WithMockUser(roles = "GESTOR")
  void gestorAcessaPainelAdministrativo() throws Exception {
    mvc.perform(get("/usuarios")).andExpect(status().isOk()).andExpect(view().name("Usuarios"));
    mvc.perform(get("/gestao-turmas")).andExpect(status().isOk()).andExpect(view().name("Turmas"));
    mvc.perform(get("/relatorios")).andExpect(status().isOk()).andExpect(view().name("Relatorios"));
  }

  @Test @WithMockUser(roles = "GESTOR")
  void gestorNaoPodeAbrirPonto() throws Exception {
    mvc.perform(post("/api/registros/abrir").with(csrf()).contentType("application/json")
        .content("{\"tipo\":\"AULA_NORMAL\",\"turmaId\":1}"))
        .andExpect(status().isForbidden());
  }
}
