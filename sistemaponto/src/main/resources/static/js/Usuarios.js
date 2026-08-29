document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("modalUsuario");
  const form = document.getElementById("formUsuario");
  const tbody = document.getElementById("corpoUsuarios");
  const formFiltros = document.getElementById("formFiltrosUsuarios");
  const tipoUsuario = document.getElementById("perfil");
  const cursoResponsavel = document.getElementById("cursoResponsavelId");
  const campoCursoResponsavel = document.getElementById("campoCursoResponsavel");
  const tipoAjuda = document.getElementById("perfilAjuda");
  let usuarios = [];
  let cursos = [];
  let turmas = [];

  const descricoes = {
    PROFESSOR: "Registra e visualiza somente o próprio ponto nas turmas vinculadas.",
    COORDENADOR: "Registra o próprio ponto e visualiza os professores do curso, sem poder ajustá-los.",
    GESTOR: "Administra usuários, cursos e turmas, ajusta pontos e possui relatórios globais; não registra ponto.",
  };
  const esc = (valor) => {
    const elemento = document.createElement("div");
    elemento.textContent = valor ?? "";
    return elemento.innerHTML;
  };
  const val = (id) => document.getElementById(id).value.trim();

  async function carregar() {
    try {
      const [usuariosRes, cursosRes, turmasRes] = await Promise.all([
        hcFetch("/api/admin/usuarios"),
        hcFetch("/api/cursos/todos"),
        hcFetch("/api/turmas/todas"),
      ]);
      [usuarios, cursos, turmas] = await Promise.all([
        usuariosRes.json(), cursosRes.json(), turmasRes.json(),
      ]);
      popularOpcoes();
      render();
    } catch (erro) {
      hcToast(erro.message, "error");
    }
  }

  function render() {
    const busca = val("filtroUsuarioBusca").toLocaleLowerCase("pt-BR");
    const tipo = val("filtroUsuarioPerfil");
    const cursoId = Number(val("filtroUsuarioCurso")) || null;
    const turmaId = Number(val("filtroUsuarioTurma")) || null;
    const status = val("filtroUsuarioStatus");
    const ordem = val("filtroUsuarioOrdem") || "az";
    const lista = usuarios.filter((usuario) => {
      const texto = `${usuario.nome || ""} ${usuario.matricula || ""} ${usuario.email || ""}`
        .toLocaleLowerCase("pt-BR");
      const cursosDoUsuario = new Set([
        usuario.cursoResponsavel?.id,
        ...(usuario.turmas || []).map((turma) => turma.curso?.id),
      ]);
      return (!busca || texto.includes(busca))
        && (!tipo || usuario.tipoUsuario === tipo)
        && (!cursoId || cursosDoUsuario.has(cursoId))
        && (!turmaId || (usuario.turmas || []).some((turma) => turma.id === turmaId))
        && (!status || String(usuario.ativo) === status);
    }).sort((a, b) => {
      if (ordem === "recentes") return new Date(b.dataCriacao || 0) - new Date(a.dataCriacao || 0);
      const comparacao = (a.nome || "").localeCompare(b.nome || "", "pt-BR");
      return ordem === "za" ? -comparacao : comparacao;
    });
    tbody.innerHTML = lista.length ? lista.map((usuario) =>
      `<tr><td data-label="Nome"><strong>${esc(usuario.nome)}</strong></td>`
      + `<td data-label="Matrícula">${esc(usuario.matricula || "—")}</td>`
      + `<td data-label="E-mail">${esc(usuario.email)}</td>`
      + `<td data-label="Tipo">${esc(usuario.tipoUsuario)}</td>`
      + `<td data-label="Abrangência de acesso">${esc(descreverAbrangencia(usuario))}</td>`
      + `<td data-label="Status"><span class="hc-status ${usuario.ativo ? "closed" : "open"}">${usuario.ativo ? "Ativo" : "Inativo"}</span></td>`
      + `<td data-label="Ações"><button class="hc-btn secondary" data-editar="${usuario.id}"><i class="fi fi-rr-edit" aria-hidden="true"></i>Editar</button></td></tr>`
    ).join("") : '<tr><td colspan="7" class="hc-empty">Nenhum usuário encontrado.</td></tr>';
  }

  function descreverAbrangencia(usuario) {
    if (usuario.tipoUsuario === "PROFESSOR") return "Somente o próprio ponto";
    if (usuario.tipoUsuario === "COORDENADOR")
      return usuario.cursoResponsavel ? `Curso: ${usuario.cursoResponsavel.nome}` : "Curso não definido";
    return "Gestão e relatórios globais";
  }

  function popularOpcoes() {
    const filtroCurso = val("filtroUsuarioCurso");
    const filtroTurma = val("filtroUsuarioTurma");
    const opcoesCursos = cursos.map((curso) =>
      `<option value="${curso.id}">${esc(curso.codigo)} - ${esc(curso.nome)}</option>`).join("");
    const opcoesTurmas = turmas.map((turma) =>
      `<option value="${turma.id}">${esc(turma.codigo)} - ${esc(turma.nome)}</option>`).join("");
    cursoResponsavel.innerHTML = '<option value="">Selecione o curso</option>' + opcoesCursos;
    document.getElementById("turmaIds").innerHTML = opcoesTurmas;
    document.getElementById("filtroUsuarioCurso").innerHTML = '<option value="">Todos</option>' + opcoesCursos;
    document.getElementById("filtroUsuarioTurma").innerHTML = '<option value="">Todas</option>' + opcoesTurmas;
    document.getElementById("filtroUsuarioCurso").value = filtroCurso;
    document.getElementById("filtroUsuarioTurma").value = filtroTurma;
  }

  function atualizarCamposDoTipo() {
    const coordenador = tipoUsuario.value === "COORDENADOR";
    const gestor = tipoUsuario.value === "GESTOR";
    campoCursoResponsavel.hidden = !coordenador;
    cursoResponsavel.required = coordenador;
    if (!coordenador) cursoResponsavel.value = "";
    const campoTurmas = document.getElementById("turmaIds").closest(".hc-field");
    campoTurmas.hidden = gestor;
    if (gestor) [...document.getElementById("turmaIds").options].forEach((opcao) => opcao.selected = false);
    tipoAjuda.textContent = descricoes[tipoUsuario.value] || "";
  }

  function abrir(usuario = null) {
    form.reset();
    document.getElementById("usuarioId").value = usuario?.id || "";
    document.getElementById("tituloModal").textContent = usuario ? "Editar usuário" : "Novo usuário";
    document.getElementById("senha").required = !usuario;
    document.getElementById("senhaAjuda").textContent = usuario ? "(deixe vazio para manter)" : "";
    tipoUsuario.disabled = Boolean(usuario);
    if (usuario) {
      ["nome", "matricula", "cpf", "telefone", "email"].forEach((campo) =>
        document.getElementById(campo).value = usuario[campo] || "");
      tipoUsuario.value = usuario.tipoUsuario;
      document.getElementById("ativo").value = String(usuario.ativo);
      cursoResponsavel.value = usuario.cursoResponsavel?.id || "";
      const ids = new Set((usuario.turmas || []).map((turma) => turma.id));
      [...document.getElementById("turmaIds").options].forEach((opcao) =>
        opcao.selected = ids.has(Number(opcao.value)));
    }
    atualizarCamposDoTipo();
    modal.classList.add("open");
    document.getElementById("nome").focus();
  }

  document.getElementById("novoUsuario").addEventListener("click", () => abrir());
  tipoUsuario.addEventListener("change", atualizarCamposDoTipo);
  document.addEventListener("click", (evento) => {
    if (evento.target.matches("[data-fechar]")) modal.classList.remove("open");
    const id = evento.target.closest("[data-editar]")?.dataset.editar;
    if (id) abrir(usuarios.find((usuario) => usuario.id === Number(id)));
  });
  modal.addEventListener("click", (evento) => {
    if (evento.target === modal) modal.classList.remove("open");
  });

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    const id = val("usuarioId");
    const botao = form.querySelector("button[type=submit]");
    botao.disabled = true;
    const tipo = tipoUsuario.value;
    const body = {
      nome: val("nome"), matricula: val("matricula"), cpf: val("cpf"),
      telefone: val("telefone"), email: val("email"), tipoUsuario: tipo,
      senha: val("senha") || null, ativo: val("ativo") === "true",
      cursoResponsavelId: tipo === "COORDENADOR" ? Number(cursoResponsavel.value) || null : null,
      turmaIds: tipo === "GESTOR" ? [] : [...document.getElementById("turmaIds").selectedOptions]
        .map((opcao) => Number(opcao.value)),
    };
    try {
      await hcFetch(id ? `/api/admin/usuarios/${id}` : "/api/admin/usuarios", {
        method: id ? "PUT" : "POST", body: JSON.stringify(body),
      });
      hcToast("Usuário salvo com sucesso.");
      modal.classList.remove("open");
      await carregar();
    } catch (erro) {
      hcToast(erro.message, "error");
    } finally {
      botao.disabled = false;
    }
  });

  formFiltros.addEventListener("submit", (evento) => {
    evento.preventDefault();
    hcSetFiltersActive("filtrosUsuarios", temFiltros());
    hcCloseFilters("filtrosUsuarios");
    render();
  });
  document.querySelectorAll("[data-limpar-usuarios]").forEach((botao) =>
    botao.addEventListener("click", () => {
      formFiltros.reset();
      hcSetFiltersActive("filtrosUsuarios", false);
      hcCloseFilters("filtrosUsuarios");
      render();
    }));
  function temFiltros() {
    return Boolean(val("filtroUsuarioBusca") || val("filtroUsuarioPerfil")
      || val("filtroUsuarioCurso") || val("filtroUsuarioTurma")
      || val("filtroUsuarioStatus") || ["za", "recentes"].includes(val("filtroUsuarioOrdem")));
  }

  carregar();
});
