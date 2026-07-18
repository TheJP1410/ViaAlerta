package com.viaalerta.backend.repository;

import com.viaalerta.backend.entity.ZonaRiesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaRiesgoRepository extends JpaRepository<ZonaRiesgo, Long> {
}