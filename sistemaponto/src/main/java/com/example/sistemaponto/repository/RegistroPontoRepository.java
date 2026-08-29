package com.example.sistemaponto.repository;

import com.example.sistemaponto.entity.RegistroPonto;
import com.example.sistemaponto.enums.StatusPonto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface RegistroPontoRepository
    extends JpaRepository<RegistroPonto, Long> {

  Optional<RegistroPonto> findByUsuarioIdAndStatus(
      Long usuarioId,
      StatusPonto status);

  List<RegistroPonto> findAllByOrderByEntradaDesc();

  List<RegistroPonto> findByUsuarioIdOrderByEntradaDesc(Long usuarioId);

  List<RegistroPonto> findByEntradaBetweenOrderByEntradaDesc(LocalDateTime inicio, LocalDateTime fim);
}
