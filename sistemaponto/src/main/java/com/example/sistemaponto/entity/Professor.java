package com.example.sistemaponto.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "professor")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Professor extends Usuario {

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "professor_turma",
      joinColumns = @JoinColumn(name = "professor_id"),
      inverseJoinColumns = @JoinColumn(name = "turma_id"))
  private Set<Turma> turmas = new LinkedHashSet<>();

  @Override
  public String getTipoUsuario() { return "PROFESSOR"; }

  public Set<Turma> getTurmas() { return turmas; }
  public void setTurmas(Set<Turma> turmas) {
    this.turmas = turmas == null ? new LinkedHashSet<>() : turmas;
  }
}
