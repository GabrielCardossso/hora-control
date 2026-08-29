package com.example.sistemaponto.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "coordenador")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Coordenador extends Usuario {

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "curso_responsavel_id", nullable = false)
  private Curso cursoResponsavel;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "coordenador_turma",
      joinColumns = @JoinColumn(name = "coordenador_id"),
      inverseJoinColumns = @JoinColumn(name = "turma_id"))
  private Set<Turma> turmas = new LinkedHashSet<>();

  @Override
  public String getTipoUsuario() { return "COORDENADOR"; }

  public Curso getCursoResponsavel() { return cursoResponsavel; }
  public void setCursoResponsavel(Curso cursoResponsavel) { this.cursoResponsavel = cursoResponsavel; }
  public Set<Turma> getTurmas() { return turmas; }
  public void setTurmas(Set<Turma> turmas) {
    this.turmas = turmas == null ? new LinkedHashSet<>() : turmas;
  }
}
