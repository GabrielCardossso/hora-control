# ⏱️ HoraControl

Sistema web de **controle de ponto educacional**: professores e coordenadores registram entrada e saída vinculadas às suas turmas, e o gestor educacional administra cadastros, ajustes e relatórios.

> Projeto acadêmico desenvolvido no curso **Técnico em Desenvolvimento de Sistemas** do SENAC.

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

## Funcionalidades

- **Três perfis de acesso** (Professor, Coordenador e Gestor educacional), com autorização por tipo de usuário via Spring Security
- **Registro de ponto** com horário do servidor, sempre vinculado a uma turma ativa e com no máximo um ponto aberto por pessoa
- **Consulta com escopo por perfil**: o coordenador vê apenas os professores do curso pelo qual é responsável
- **Ajustes de ponto** feitos só pelo gestor, sempre com justificativa e trilha de auditoria
- **Relatórios em PDF** (OpenPDF), com o escopo do usuário garantido no back-end, mesmo se a URL for manipulada
- **Exclusão lógica** de usuários e turmas, preservando o histórico
- **Migrações versionadas** com Flyway

## Tecnologias

| Camada | Stack |
| --- | --- |
| Back-end | Java 17, Spring Boot 3.5 (MVC, Security, Validation, Data JPA) |
| Front-end | Thymeleaf, HTML, CSS e JavaScript |
| Banco de dados | PostgreSQL + Flyway |
| Relatórios | OpenPDF |
| Testes | JUnit, Spring Security Test e H2 em memória |

## Como executar

O projeto Spring Boot fica na pasta [`sistemaponto/`](sistemaponto).

1. Crie no PostgreSQL o banco `sistemaPonto`.
2. Em `sistemaponto/`, copie `.env.example.properties` para `.env.properties` e configure a conexão e a senha inicial do gestor.
3. Rode a aplicação:

```bash
cd sistemaponto
./mvnw spring-boot:run
```

No Windows, use `.\mvnw.cmd spring-boot:run`. Depois, acesse `http://localhost:8080`.

Para rodar os testes:

```bash
./mvnw clean test
```

## Documentação

- [README técnico](sistemaponto/README.md): perfis e permissões, regras de negócio, banco e migrações
- [Arquitetura atual](sistemaponto/docs/ARQUITETURA_ATUAL.md)
- [Modelagem do banco de dados](sistemaponto/docs/MODELAGEM_BANCO_DADOS.md)
