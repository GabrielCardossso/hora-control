# Modelagem do banco de dados

## Tabelas principais

| Tabela | Responsabilidade |
|---|---|
| `usuarios` | Identidade, autenticação e dados comuns de todas as contas. |
| `professor` | Subtipo de usuário professor. |
| `coordenador` | Subtipo com `curso_responsavel_id` obrigatório. |
| `gestor` | Subtipo administrativo. |
| `curso` | Curso acadêmico. |
| `turma` | Turma pertencente a um curso. |
| `professor_turma` | Turmas em que o professor pode registrar ponto. |
| `coordenador_turma` | Turmas em que o coordenador pessoalmente pode registrar ponto. |
| `registro_ponto` | Entrada, saída, atividade, status e auditoria. |

## Relacionamentos

```text
usuarios 1---0..1 professor
usuarios 1---0..1 coordenador ---N:1 curso
usuarios 1---0..1 gestor
professor N---N turma
coordenador N---N turma
curso 1---N turma
usuarios 1---N registro_ponto
gestor 1---N ajustes (validado pela regra de negócio)
```

Cada linha de `usuarios` possui exatamente uma linha de subtipo por regra da aplicação. A estratégia JPA `JOINED` evita colunas específicas nulas em uma tabela única e elimina o antigo campo textual `perfil`.

## Integridade

- Índices únicos case-insensitive para e-mail e código de turma.
- Matrícula e CPF únicos quando preenchidos.
- Chaves estrangeiras nos vínculos de usuários, turmas, cursos e auditoria.
- Índice `idx_registro_usuario_entrada` para histórico cronológico.
- Índice parcial `uk_registro_ponto_aberto_usuario` para impedir dois pontos abertos do mesmo usuário.
- Inativação lógica de usuários, cursos e turmas preserva o histórico.

## Migração V3

- `PROFESSOR` vira subclasse `Professor`.
- `COORDENADOR_CURSO` com curso válido vira `Coordenador`.
- `ADMIN` vira `Gestor`.
- `COORDENADOR_NUCLEO`, `COORDENADOR_EIXO` e coordenadores sem curso são preservados como professores inativos, sem herdar privilégios globais.
- IDs, senhas BCrypt, registros e vínculos compatíveis são mantidos.
