CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    matricula VARCHAR(50),
    email VARCHAR(180) NOT NULL,
    cpf VARCHAR(14),
    telefone VARCHAR(30),
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO usuarios (id, nome, matricula, email, cpf, telefone, senha, ativo, data_criacao)
SELECT id, nome, matricula, email, cpf, telefone, senha,
       CASE
         WHEN perfil IN ('COORDENADOR_NUCLEO', 'COORDENADOR_EIXO') THEN FALSE
         WHEN perfil = 'COORDENADOR_CURSO' AND curso_responsavel_id IS NULL THEN FALSE
         ELSE ativo
       END,
       data_criacao
FROM professores;

SELECT setval(pg_get_serial_sequence('usuarios', 'id'),
              COALESCE((SELECT MAX(id) FROM usuarios), 1),
              EXISTS (SELECT 1 FROM usuarios));

CREATE UNIQUE INDEX uk_usuario_email ON usuarios (LOWER(email));
CREATE UNIQUE INDEX uk_usuario_matricula ON usuarios (matricula) WHERE matricula IS NOT NULL;
CREATE UNIQUE INDEX uk_usuario_cpf ON usuarios (cpf) WHERE cpf IS NOT NULL;

CREATE TABLE professor (
    usuario_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_professor_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE coordenador (
    usuario_id BIGINT PRIMARY KEY,
    curso_responsavel_id BIGINT NOT NULL,
    CONSTRAINT fk_coordenador_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_coordenador_curso FOREIGN KEY (curso_responsavel_id) REFERENCES curso(id)
);

CREATE TABLE gestor (
    usuario_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_gestor_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT INTO professor (usuario_id)
SELECT id FROM professores
WHERE perfil = 'PROFESSOR'
   OR perfil IN ('COORDENADOR_NUCLEO', 'COORDENADOR_EIXO')
   OR (perfil = 'COORDENADOR_CURSO' AND curso_responsavel_id IS NULL);

INSERT INTO coordenador (usuario_id, curso_responsavel_id)
SELECT id, curso_responsavel_id FROM professores
WHERE perfil = 'COORDENADOR_CURSO' AND curso_responsavel_id IS NOT NULL;

INSERT INTO gestor (usuario_id)
SELECT id FROM professores WHERE perfil = 'ADMIN';

CREATE TABLE coordenador_turma (
    coordenador_id BIGINT NOT NULL,
    turma_id BIGINT NOT NULL,
    PRIMARY KEY (coordenador_id, turma_id),
    CONSTRAINT fk_coordenador_turma_coordenador FOREIGN KEY (coordenador_id)
      REFERENCES coordenador(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_coordenador_turma_turma FOREIGN KEY (turma_id)
      REFERENCES turma(id) ON DELETE CASCADE
);

INSERT INTO coordenador_turma (coordenador_id, turma_id)
SELECT pt.professor_id, pt.turma_id
FROM professor_turma pt
JOIN coordenador c ON c.usuario_id = pt.professor_id;

ALTER TABLE professor_turma DROP CONSTRAINT IF EXISTS fk_professor_turma_professor;
DELETE FROM professor_turma WHERE professor_id NOT IN (SELECT usuario_id FROM professor);
ALTER TABLE professor_turma ADD CONSTRAINT fk_professor_turma_professor
  FOREIGN KEY (professor_id) REFERENCES professor(usuario_id) ON DELETE CASCADE;

ALTER TABLE registro_ponto ADD COLUMN usuario_id BIGINT;
UPDATE registro_ponto SET usuario_id = professor_id;
ALTER TABLE registro_ponto ALTER COLUMN usuario_id SET NOT NULL;
ALTER TABLE registro_ponto DROP CONSTRAINT IF EXISTS fk_registro_professor;
ALTER TABLE registro_ponto DROP COLUMN professor_id;
ALTER TABLE registro_ponto ADD CONSTRAINT fk_registro_usuario
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id);

ALTER TABLE registro_ponto DROP CONSTRAINT IF EXISTS fk_registro_alterado_por;
ALTER TABLE registro_ponto ADD CONSTRAINT fk_registro_alterado_por
  FOREIGN KEY (alterado_por_id) REFERENCES usuarios(id);

DROP INDEX IF EXISTS idx_registro_professor_entrada;
DROP INDEX IF EXISTS uk_registro_ponto_aberto_professor;
CREATE INDEX idx_registro_usuario_entrada ON registro_ponto (usuario_id, entrada DESC);
CREATE UNIQUE INDEX uk_registro_ponto_aberto_usuario
  ON registro_ponto (usuario_id) WHERE status = 'ABERTO';

ALTER TABLE professores DROP CONSTRAINT IF EXISTS fk_professor_curso;
DROP TABLE professores;
