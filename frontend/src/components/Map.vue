<template>
  <div ref="mapContainer" class="w-full h-full z-0 rounded-xl shadow-inner border border-gray-200 map-container"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import L from 'leaflet';
import type { AccidentPoint } from '../services/api';

// Corregir iconos de Leaflet
const defaultIcon = L.icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});
L.Marker.prototype.options.icon = defaultIcon;

// --- Props ---
const props = withDefaults(
  defineProps<{
    points: AccidentPoint[];
    centerLat?: number;
    centerLng?: number;
    zoom?: number;
  }>(),
  {
    centerLat: -12.046374,
    centerLng: -77.042793,
    zoom: 12,
  }
);

// --- Emits ---
const emit = defineEmits<{
  (e: 'markerClick', point: AccidentPoint): void;
}>();

// --- Refs ---
const mapContainer = ref<HTMLElement | null>(null);
let map: L.Map | null = null;
let markersLayer: L.LayerGroup | null = null;
let userLocationMarker: L.CircleMarker | null = null;

// --- Helpers ---
function getRiskColor(riskLevel: string): string {
  switch (riskLevel) {
    case 'Crítico': return '#991b1b';
    case 'Alto':    return '#ef4444';
    case 'Medio':   return '#f59e0b';
    default:        return '#6b7280';
  }
}

function renderMarkers(points: AccidentPoint[]) {
  if (!map) return;

  if (markersLayer) {
    markersLayer.clearLayers();
  } else {
    markersLayer = L.layerGroup().addTo(map);
  }

  points.forEach((point) => {
    const color = getRiskColor(point.riskLevel);

    const circle = L.circleMarker([point.lat, point.lng], {
      color: color,
      fillColor: color,
      fillOpacity: 0.65,
      weight: 2,
      radius: 12,
    });

    circle.bindPopup(`
      <div style="font-family: 'Inter', sans-serif; padding: 4px;">
        <h3 style="font-weight:700; color:#111827; font-size:13px; margin:0 0 4px 0;">${point.intersection}</h3>
        <p style="font-size:11px; color:#6b7280; margin:0 0 2px 0;">
          Riesgo: <span style="font-weight:700; color:${color}">${point.riskLevel}</span>
        </p>
        <p style="font-size:11px; color:#6b7280; margin:0;">
          Casos en 2025: <span style="font-weight:700;">${point.incidents}</span>
        </p>
      </div>
    `);

    circle.on('click', () => emit('markerClick', point));

    markersLayer!.addLayer(circle);
  });
}

// --- Métodos públicos (expuestos a componentes padre) ---

/** Vuela el mapa a una coordenada con animación cinemática */
function flyTo(lat: number, lng: number, zoom?: number) {
  if (!map) return;
  map.flyTo([lat, lng], zoom ?? map.getZoom(), {
    duration: 1.2,
    easeLinearity: 0.25,
  });
}

/** Centra el mapa en la ubicación real del usuario via Geolocation API */
function locateUser(): Promise<{ lat: number; lng: number }> {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Geolocalización no soportada'));
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude: lat, longitude: lng } = position.coords;

        // Mostrar marcador azul pulsante en la ubicación del usuario
        if (map) {
          if (userLocationMarker) {
            userLocationMarker.setLatLng([lat, lng]);
          } else {
            userLocationMarker = L.circleMarker([lat, lng], {
              color: '#3b82f6',
              fillColor: '#3b82f6',
              fillOpacity: 0.3,
              weight: 3,
              radius: 10,
              className: 'user-location-pulse',
            }).addTo(map);
          }
          flyTo(lat, lng, 15);
        }

        resolve({ lat, lng });
      },
      (error) => {
        reject(error);
      },
      { enableHighAccuracy: true, timeout: 8000 }
    );
  });
}

// Exponer métodos al componente padre via defineExpose
defineExpose({ flyTo, locateUser });

// --- Lifecycle ---
onMounted(() => {
  if (!mapContainer.value) return;

  map = L.map(mapContainer.value, {
    zoomControl: false,
  }).setView([props.centerLat, props.centerLng], props.zoom);

  L.control.zoom({ position: 'bottomright' }).addTo(map);

  L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a> &copy; <a href="https://carto.com/attributions">CARTO</a>',
    subdomains: 'abcd',
    maxZoom: 20,
  }).addTo(map);

  renderMarkers(props.points);
});

// Re-renderizar marcadores si los puntos cambian
watch(() => props.points, (newPoints) => {
  renderMarkers(newPoints);
}, { deep: true });

// Volar hacia el nuevo centro cuando cambian las coordenadas
watch(
  [() => props.centerLat, () => props.centerLng],
  ([newLat, newLng], [oldLat, oldLng]) => {
    if (map && (newLat !== oldLat || newLng !== oldLng)) {
      map.flyTo([newLat, newLng], map.getZoom(), {
        duration: 1.2,
        easeLinearity: 0.25,
      });
    }
  }
);
</script>

<style scoped>
.map-container {
  min-height: 400px;
}
</style>

<style>
/* Animación pulsante para el marcador de ubicación del usuario */
@keyframes pulse-ring {
  0% { opacity: 0.6; transform: scale(1); }
  100% { opacity: 0; transform: scale(2.5); }
}
.user-location-pulse {
  animation: pulse-ring 1.5s ease-out infinite;
}
</style>
