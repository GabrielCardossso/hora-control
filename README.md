<img src="./.github/readme/header.svg" width="100%" alt="hora-control: Projeto acadêmico · SENAC" />

<img src="./.github/readme/sobre.svg" width="100%" alt="Sobre o projeto: Sistema web de controle de ponto educacional: professores e coordenadores registram entrada e saída vinculadas às suas turmas, e o gestor educacional administra cadastros, ajustes e relatórios." />

<img src="./.github/readme/stack.svg" width="100%" alt="Stack: Java 17, Spring Boot, Spring Security, Spring Data JPA, Thymeleaf, HTML, CSS, JavaScript, PostgreSQL, Flyway, OpenPDF, JUnit, H2, Maven" />

<img src="./.github/readme/funcionalidades.svg" width="100%" alt="Funcionalidades: Três perfis de acesso (Professor, Coordenador e Gestor educacional, com autorização por tipo de usuário via Spring Security.); Registro de ponto (Horário do servidor, sempre vinculado a uma turma ativa e com no máximo um ponto aberto por pessoa.); Consulta por perfil (O coordenador vê apenas os professores do curso pelo qual é responsável.); Ajustes auditados (Só o gestor ajusta ponto, sempre com justificativa e trilha de auditoria.); Relatórios em PDF (Gerados com OpenPDF; o escopo do usuário é garantido no back-end, mesmo se a URL for manipulada.); Histórico preservado (Exclusão lógica de usuários e turmas, e migrações versionadas com Flyway.)" />

<img src="./.github/readme/estrutura.svg" width="100%" alt="Estrutura de pastas: sistemaponto/ (aplicação Spring Boot), src/ (código, telas Thymeleaf e testes), docs/ (arquitetura e modelagem do banco), pom.xml (dependências (Maven)), INOVA/ (materiais do INOVA Senac), video/ (vídeo-pitch do projeto), README.md" />

<img src="./.github/readme/secao-01-como-rodar.svg" width="100%" alt="Como rodar" />

1. Crie no PostgreSQL o banco `sistemaPonto`.
2. Em [`sistemaponto/`](sistemaponto), copie `.env.example.properties` para `.env.properties` e configure a conexão e a senha inicial do gestor.
3. Suba a aplicação e acesse `http://localhost:8080`:

```bash
cd sistemaponto
./mvnw spring-boot:run        # no Windows: .\mvnw.cmd spring-boot:run
```

Para rodar os testes (JUnit + Spring Security Test, com H2 em memória):

```bash
./mvnw clean test
```

<img src="./.github/readme/secao-02-documentacao.svg" width="100%" alt="Documentação" />

| Documento | O que tem |
| --- | --- |
| [README técnico](sistemaponto/README.md) | Perfis e permissões, regras de negócio, banco e migrações |
| [Arquitetura atual](sistemaponto/docs/ARQUITETURA_ATUAL.md) | Como o código está organizado |
| [Modelagem do banco](sistemaponto/docs/MODELAGEM_BANCO_DADOS.md) | Tabelas e relacionamentos |

<a href="https://github.com/GabrielCardossso"><img src="./.github/readme/rodape.svg" width="100%" alt="Obrigado! Feito por Gabriel Cardoso" /></a>
