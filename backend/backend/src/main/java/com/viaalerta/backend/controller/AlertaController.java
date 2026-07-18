package com.viaalerta.backend.controller;

import com.viaalerta.backend.dto.AlertaResponse;
import com.viaalerta.backend.dto.DashboardStatsResponse;
import com.viaalerta.backend.dto.PuntoCriticoResponse;
import com.viaalerta.backend.dto.UbicacionRequest;
import com.viaalerta.backend.entity.AlertaEnviada;
import com.viaalerta.backend.entity.ZonaRiesgo;
import com.viaalerta.backend.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/zonas")
    public ResponseEntity<List<ZonaRiesgo>> obtenerZonas() {
        return ResponseEntity.ok(alertaService.obtenerTodasLasZonas());
    }

    @PostMapping("/ubicacion/verificar")
    public ResponseEntity<List<AlertaResponse>> verificarUbicacion(@Valid @RequestBody UbicacionRequest request) {
        List<AlertaEnviada> alertas = alertaService.verificarUbicacion(
                request.getLatitud(),
                request.getLongitud(),
                request.getTipoTransporte()
        );

        List<AlertaResponse> respuesta = alertas.stream()
                .map(alerta -> new AlertaResponse(
                        alerta.getZona().getNombre(),
                        alerta.getMensaje(),
                        alerta.getZona().getNivelRiesgo(),
                        alerta.getZona().getLatitud(),
                        alerta.getZona().getLongitud()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/alertas/historial")
    public ResponseEntity<List<AlertaEnviada>> obtenerHistorial() {
        return ResponseEntity.ok(alertaService.obtenerHistorialAlertas());
    }
    @GetMapping("/puntos-criticos")
    public ResponseEntity<List<PuntoCriticoResponse>> obtenerPuntosCriticos(
            @RequestParam(required = false) String district) {
        if (district != null && !district.isBlank()) {
            return ResponseEntity.ok(alertaService.obtenerPuntosPorDistrito(district));
        }
        return ResponseEntity.ok(alertaService.obtenerPuntosCriticos());
    }

    @GetMapping("/puntos-criticos/{id}")
    public ResponseEntity<PuntoCriticoResponse> obtenerPuntoCriticoPorId(@PathVariable Long id) {
        PuntoCriticoResponse punto = alertaService.obtenerPuntoCriticoPorId(id);
        if (punto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(punto);
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> obtenerEstadisticas() {
        return ResponseEntity.ok(alertaService.obtenerEstadisticas());
    }
}