package com.viaalerta.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_enviada")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaEnviada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaRiesgo zona;

    @Column(name = "tipo_transporte", nullable = false)
    private String tipoTransporte;

    @Column(name = "latitud_usuario", nullable = false)
    private Double latitudUsuario;

    @Column(name = "longitud_usuario", nullable = false)
    private Double longitudUsuario;

    private String mensaje;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
}