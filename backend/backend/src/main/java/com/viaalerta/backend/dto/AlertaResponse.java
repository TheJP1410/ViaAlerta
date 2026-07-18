package com.viaalerta.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaResponse {
    private String nombreZona;
    private String mensaje;
    private String nivelRiesgo;
    private Double latitudZona;
    private Double longitudZona;
}