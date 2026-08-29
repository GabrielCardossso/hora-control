# HoraControl

Sistema web de controle de ponto educacional com autenticação, autorização por tipo concreto de usuário, vínculo com turmas e relatórios PDF.

## Tecnologias

- Java 17 e Spring Boot 3.5
- Spring MVC, Security, Validation e Data JPA
- Thymeleaf, HTML, CSS e JavaScript
- PostgreSQL e Flyway
- OpenPDF
- H2 isolado para testes

## Execução local

1. Crie no PostgreSQL o banco `sistemaPonto`.
2. Copie `.env.example.properties` para `.env.properties`.
3. Configure a conexão e a senha inicial do gestor.
4. Execute `./mvnw spring-boot:run` ou, no Windows, `.\mvnw.cmd spring-boot:run`.
5. Acesse `http://localhost:8080`.

O arquivo `.env.properties` é ignorado pelo Git. Em produção, use `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `GESTOR_EMAIL` e `GESTOR_PASSWORD`.

## Usuários e permissões

O acesso não é armazenado em enum. `Usuario` é uma entidade abstrata e cada papel é uma subclasse JPA.

| Tipo | Registro de ponto | Consulta | Ajuste | PDF | Cadastros |
|---|---|---|---|---|---|
| Professor | Próprio, somente em turmas vinculadas | Somente o próprio | Não | Somente o próprio | Não |
| Coordenador | Próprio, somente em turmas vinculadas | Próprio e professores do curso responsável | Não | Somente o próprio | Não |
| Gestor educacional | Não registra | Global | Global | Filtrado ou global | Usuários, cursos e turmas |

Não existe cadastro público. O gestor cria e inativa as contas. O tipo concreto de uma conta existente é imutável; para trocar o tipo, a conta deve ser inativada e recriada, preservando a integridade do histórico.

## Regras principais

- Cada professor ou coordenador pode possuir somente um ponto aberto.
- Entrada e saída usam o horário do servidor.
- Todo registro exige uma turma ativa vinculada ao usuário.
- O curso responsável limita a consulta do coordenador aos registros dos professores daquele curso.
- Coordenadores nunca ajustam pontos alheios.
- Somente o gestor ajusta registros, sempre com justificativa e trilha de auditoria.
- PDFs de professor e coordenador são forçados no backend para o próprio usuário, mesmo se a URL for manipulada.
- E-mail, matrícula, CPF e código de turma possuem unicidade quando informados.
- Exclusões de usuário e turma são lógicas para preservar o histórico.

## Banco e migrações

O Flyway executa os scripts de `src/main/resources/db/migration`. A migração V3 converte a antiga tabela polimórfica `professores` na hierarquia `usuarios`, `professor`, `coordenador` e `gestor`, preservando IDs, vínculos e registros. Antigos coordenadores de núcleo/eixo são preservados como contas inativas, pois não existe equivalência segura no novo modelo.

O Hibernate usa `ddl-auto=validate`: a aplicação não recria nem apaga o banco ao iniciar.

## Testes

```powershell
.\mvnw.cmd clean test
```

Os testes usam H2 em memória e cobrem autenticação/autorização, hierarquia JPA, limites de consulta, registro e ajuste de ponto e geração de PDF.

## Estrutura

- `config`: segurança, autenticação e criação do gestor inicial.
- `controller`: páginas e endpoints HTTP.
- `dto`: contratos de entrada.
- `entity`: domínio e hierarquia JPA.
- `enums`: apenas estado e tipo de registro; não contém papéis de acesso.
- `exception`: respostas de erro padronizadas.
- `repository`: persistência JPA.
- `service`: regras de autorização de domínio e relatórios.
- `templates` e `static`: interface web.
- `db/migration`: evolução versionada do PostgreSQL.
