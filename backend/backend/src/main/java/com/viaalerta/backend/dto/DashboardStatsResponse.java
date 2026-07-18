package com.viaalerta.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Integer totalIncidents;
    private Integer criticalPoints;
    private Integer totalZones;
    private String topDistrict;
}