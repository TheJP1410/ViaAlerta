<template>
  <div ref="mapContainer" class="w-full h-full z-0 rounded-xl shadow-inner border border-gray-200 map-container"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import L from 'leaflet';
import type { AccidentPoint } from '../services/api';
import { useDarkMode } from '../composables/useDarkMode';

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
let userLocationMarker: L.Marker | null = null;
let tileLayer: L.TileLayer | null = null;

const { isDark } = useDarkMode();

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
        <h3 style="font-weight:700; color:var(--popup-title, #111827); font-size:13px; margin:0 0 4px 0;">${point.intersection}</h3>
        <p style="font-size:11px; color:var(--popup-text, #6b7280); margin:0 0 2px 0;">
          Riesgo: <span style="font-weight:700; color:${color}">${point.riskLevel}</span>
        </p>
        <p style="font-size:11px; color:var(--popup-text, #6b7280); margin:0;">
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
            const pulseIcon = L.divIcon({
              className: 'user-location-wrapper',
              html: '<div class="user-location-pulse"></div><div class="user-location-dot"></div>',
              iconSize: [20, 20],
              iconAnchor: [10, 10],
            });
            userLocationMarker = L.marker([lat, lng], {
              icon: pulseIcon,
              zIndexOffset: 1000
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

function updateTileLayer(dark: boolean) {
  if (!map) return;
  if (tileLayer) {
    map.removeLayer(tileLayer);
  }
  
  const tileUrl = dark 
    ? 'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png'
    : 'https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png';
    
  tileLayer = L.tileLayer(tileUrl, {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a> &copy; <a href="https://carto.com/attributions">CARTO</a>',
    subdomains: 'abcd',
    maxZoom: 20,
  }).addTo(map);
}

// --- Lifecycle ---
onMounted(() => {
  if (!mapContainer.value) return;

  map = L.map(mapContainer.value, {
    zoomControl: false,
  }).setView([props.centerLat, props.centerLng], props.zoom);

  L.control.zoom({ position: 'bottomright' }).addTo(map);

  updateTileLayer(isDark.value);
  renderMarkers(props.points);
});

// Cambiar tiles si cambia el modo oscuro
watch(isDark, (newDark) => {
  updateTileLayer(newDark);
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
.user-location-wrapper {
  position: relative;
}
.user-location-dot {
  width: 20px;
  height: 20px;
  background-color: #3b82f6;
  border-radius: 50%;
  border: 3px solid #ffffff;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 2;
  box-shadow: 0 0 10px rgba(0,0,0,0.3);
}
.dark .user-location-dot {
  border-color: #1f2937;
}
.user-location-pulse {
  width: 20px;
  height: 20px;
  background-color: #3b82f6;
  border-radius: 50%;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  animation: pulse-ring 2s cubic-bezier(0.215, 0.61, 0.355, 1) infinite;
}
@keyframes pulse-ring {
  0% { transform: scale(0.9); opacity: 0.8; }
  100% { transform: scale(3.5); opacity: 0; }
}

/* Variables para popups de Leaflet en dark mode */
.dark .leaflet-popup-content-wrapper {
  background: #1f2937;
  color: #f3f4f6;
}
.dark .leaflet-popup-tip {
  background: #1f2937;
}
.dark {
  --popup-title: #f9fafb;
  --popup-text: #9ca3af;
}
</style>
