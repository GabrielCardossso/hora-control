package com.example.sistemaponto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String nome;

  @Column(unique = true, length = 50)
  private String matricula;

  @Column(unique = true, nullable = false, length = 180)
  private String email;

  @Column(unique = true, length = 14)
  private String cpf;

  @Column(length = 30)
  private String telefone;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column(nullable = false)
  private String senha;

  @Column(nullable = false)
  private boolean ativo = true;

  @Column(nullable = false)
  private LocalDateTime dataCriacao = LocalDateTime.now();

  @Transient
  @JsonProperty("tipoUsuario")
  public abstract String getTipoUsuario();

  @Transient
  public boolean podeRegistrarPonto() {
    return !(this instanceof Gestor);
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getMatricula() { return matricula; }
  public void setMatricula(String matricula) { this.matricula = matricula; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getCpf() { return cpf; }
  public void setCpf(String cpf) { this.cpf = cpf; }
  public String getTelefone() { return telefone; }
  public void setTelefone(String telefone) { this.telefone = telefone; }
  public String getSenha() { return senha; }
  public void setSenha(String senha) { this.senha = senha; }
  public boolean isAtivo() { return ativo; }
  public void setAtivo(boolean ativo) { this.ativo = ativo; }
  public LocalDateTime getDataCriacao() { return dataCriacao; }
  public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
