package com.viaalerta.backend.config;

import com.viaalerta.backend.entity.ZonaRiesgo;
import com.viaalerta.backend.repository.ZonaRiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ZonaRiesgoRepository zonaRiesgoRepository;

    @Override
    public void run(String... args) {
        if (zonaRiesgoRepository.count() > 0) {
            System.out.println("DataSeeder: ya existen zonas, no se siembra nada.");
            return;
        }

        List<ZonaRiesgo> zonasIniciales = List.of(
                crearZona(
                        "Panamericana Norte - Puente Piedra", -11.8617, -77.0850, 400,
                        "Choques y atropellos", "Alto", 32,
                        "PNP - Divpiat (vía identificada como de alta siniestralidad)",
                        "Puente Piedra", "Exceso de velocidad",
                        List.of("Motociclistas", "Peatones")
                ),
                crearZona(
                        "Panamericana Sur - Villa El Salvador", -12.2180, -76.9420, 400,
                        "Choques y atropellos", "Alto", 28,
                        "PNP - Divpiat (vía identificada como de alta siniestralidad)",
                        "Villa El Salvador", "Imprudencia del conductor",
                        List.of("Peatones", "Ciclistas")
                ),
                crearZona(
                        "Carretera Central - Santa Anita", -12.0430, -76.9670, 350,
                        "Choques vehiculares", "Alto", 25,
                        "PNP - Divpiat (distrito con mayor concentración de siniestros)",
                        "Santa Anita", "Imprudencia del conductor",
                        List.of("Motociclistas", "Ocupantes de bus")
                ),
                crearZona(
                        "Av. Próceres de la Independencia - SJL", -11.9990, -77.0000, 300,
                        "Atropellos", "Alto", 20,
                        "PNP - Divpiat (distrito con mayor concentración de siniestros)",
                        "San Juan de Lurigancho", "Falta de señalización",
                        List.of("Peatones")
                ),
                crearZona(
                        "Vía de Evitamiento - Ate", -12.0270, -76.9280, 350,
                        "Choques vehiculares", "Medio", 15,
                        "PNP - Divpiat",
                        "Ate", "Exceso de velocidad",
                        List.of("Ocupantes de bus", "Peatones")
                )
        );

        zonaRiesgoRepository.saveAll(zonasIniciales);
        System.out.println("DataSeeder: " + zonasIniciales.size() + " zonas de riesgo sembradas automaticamente.");
    }

    private ZonaRiesgo crearZona(String nombre, double lat, double lng, int radio,
                                 String tipo, String nivel, int incidentes, String fuente,
                                 String distrito, String causaComun, List<String> victimas) {
        ZonaRiesgo zona = new ZonaRiesgo();
        zona.setNombre(nombre);
        zona.setLatitud(lat);
        zona.setLongitud(lng);
        zona.setRadioMetros(radio);
        zona.setTipoIncidente(tipo);
        zona.setNivelRiesgo(nivel);
        zona.setNumIncidentes(incidentes);
        zona.setFuente(fuente);
        zona.setFechaActualizacion(LocalDateTime.now());
        zona.setDistrito(distrito);
        zona.setCausaComun(causaComun);
        zona.setVictimasFrecuentes(victimas);
        return zona;
    }
}