package com.viaalerta.backend.repository;

import com.viaalerta.backend.entity.AlertaEnviada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaEnviadaRepository extends JpaRepository<AlertaEnviada, Long> {
}