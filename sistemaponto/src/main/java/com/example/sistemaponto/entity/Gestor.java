package com.example.sistemaponto.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "gestor")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Gestor extends Usuario {
  @Override
  public String getTipoUsuario() { return "GESTOR"; }
}
