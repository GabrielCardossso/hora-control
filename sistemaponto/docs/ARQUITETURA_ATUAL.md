# Arquitetura atual do HoraControl

## Fluxo

```text
Navegador -> Spring Security -> Controller -> Service -> Repository -> PostgreSQL
```

Controllers aplicam a autorização de rota e Services reaplicam as regras sobre o usuário autenticado. Assim, alterar parâmetros no navegador não amplia o acesso.

## Domínio de usuários

```text
Usuario (abstrata)
├── Professor
├── Coordenador
└── Gestor
```

`Usuario` guarda identidade, credenciais, situação e data de criação. A herança JPA usa a estratégia `JOINED`: dados comuns ficam em `usuarios` e cada tipo possui uma tabela própria. Não há enum de perfil.

- `Professor`: possui turmas em que pode registrar ponto.
- `Coordenador`: possui um curso obrigatório sob responsabilidade e turmas em que pessoalmente atua.
- `Gestor`: não possui vínculo de registro de ponto.
- `RegistroPonto`: pertence a um `Usuario`, liga-se a uma turma e registra auditoria do gestor que fez eventual ajuste.

## Autorização

- `ROLE_PROFESSOR`: consulta e PDF próprios; abre e fecha o próprio ponto nas turmas vinculadas.
- `ROLE_COORDENADOR`: mesmas ações próprias e consulta somente dos professores do curso responsável; não ajusta registros.
- `ROLE_GESTOR`: gestão de usuários/cursos/turmas, consulta e PDF globais e ajuste auditado; não abre nem fecha ponto.

O `CustomUserDetailsService` autentica por e-mail em `usuarios` e deriva a autoridade da subclasse. Senhas usam BCrypt e não são serializadas. Relações de registros expõem apenas os dados mínimos do usuário, evitando vazar CPF, telefone ou e-mail em consultas de coordenação.

## Relatórios

A prévia e o PDF usam o mesmo método de escopo. Para professor e coordenador, o backend sempre força o ID do solicitante. Somente o gestor aceita filtros globais por usuário, turma, período, status, tipo ou texto.

## Persistência

A migração V3 preserva as chaves dos usuários antigos, move os vínculos de turma e troca as referências de `registro_ponto.professor_id` por `registro_ponto.usuario_id`. O índice parcial continua garantindo apenas um ponto aberto por usuário.
