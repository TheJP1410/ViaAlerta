package com.viaalerta.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "zona_riesgo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRiesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(name = "radio_metros", nullable = false)
    private Integer radioMetros;

    @Column(name = "tipo_incidente")
    private String tipoIncidente;

    @Column(name = "nivel_riesgo")
    private String nivelRiesgo;

    @Column(name = "num_incidentes")
    private Integer numIncidentes;

    private String fuente;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}