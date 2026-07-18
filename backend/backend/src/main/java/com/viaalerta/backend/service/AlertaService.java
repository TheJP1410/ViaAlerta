package com.viaalerta.backend.service;

import com.viaalerta.backend.entity.AlertaEnviada;
import com.viaalerta.backend.entity.ZonaRiesgo;
import com.viaalerta.backend.repository.AlertaEnviadaRepository;
import com.viaalerta.backend.repository.ZonaRiesgoRepository;
import com.viaalerta.backend.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final ZonaRiesgoRepository zonaRiesgoRepository;
    private final AlertaEnviadaRepository alertaEnviadaRepository;

    /**
     * Revisa si la ubicacion del usuario esta dentro del radio de alguna zona de riesgo.
     * Si encuentra coincidencias, genera y guarda las alertas correspondientes.
     */
    public List<AlertaEnviada> verificarUbicacion(double latitudUsuario, double longitudUsuario, String tipoTransporte) {
        List<ZonaRiesgo> todasLasZonas = zonaRiesgoRepository.findAll();

        List<AlertaEnviada> alertasGeneradas = todasLasZonas.stream()
                .filter(zona -> estaDentroDelRadio(zona, latitudUsuario, longitudUsuario))
                .map(zona -> crearAlerta(zona, latitudUsuario, longitudUsuario, tipoTransporte))
                .map(alertaEnviadaRepository::save)
                .collect(Collectors.toList());

        return alertasGeneradas;
    }

    private boolean estaDentroDelRadio(ZonaRiesgo zona, double latitudUsuario, double longitudUsuario) {
        double distancia = GeoUtils.calcularDistanciaMetros(
                zona.getLatitud(), zona.getLongitud(),
                latitudUsuario, longitudUsuario
        );
        return distancia <= zona.getRadioMetros();
    }

    private AlertaEnviada crearAlerta(ZonaRiesgo zona, double latitudUsuario, double longitudUsuario, String tipoTransporte) {
        AlertaEnviada alerta = new AlertaEnviada();
        alerta.setZona(zona);
        alerta.setTipoTransporte(tipoTransporte);
        alerta.setLatitudUsuario(latitudUsuario);
        alerta.setLongitudUsuario(longitudUsuario);
        alerta.setMensaje(generarMensaje(zona, tipoTransporte));
        alerta.setFechaHora(LocalDateTime.now());
        return alerta;
    }

    private String generarMensaje(ZonaRiesgo zona, String tipoTransporte) {
        return String.format(
                "⚠️ Alerta: te acercas a \"%s\", una zona con %s incidencia de %s. Se han registrado %d incidentes reportados por %s.",
                zona.getNombre(),
                zona.getNivelRiesgo() != null ? zona.getNivelRiesgo().toLowerCase() : "alta",
                zona.getTipoIncidente() != null ? zona.getTipoIncidente() : "accidentes",
                zona.getNumIncidentes() != null ? zona.getNumIncidentes() : 0,
                zona.getFuente() != null ? zona.getFuente() : "fuentes oficiales"
        );
    }

    public List<ZonaRiesgo> obtenerTodasLasZonas() {
        return zonaRiesgoRepository.findAll();
    }

    public List<AlertaEnviada> obtenerHistorialAlertas() {
        return alertaEnviadaRepository.findAll();
    }
}