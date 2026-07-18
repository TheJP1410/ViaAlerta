package com.viaalerta.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoCriticoResponse {
    private String id;
    private Double lat;
    private Double lng;
    private String intersection;
    private String district;
    private String riskLevel;
    private Integer incidents;
    private String lastIncident;
    private List<String> mainVictims;
    private String commonCause;
}