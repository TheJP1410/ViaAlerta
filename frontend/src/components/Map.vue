<template>
  <div ref="mapContainer" class="w-full h-full z-0 rounded-xl shadow-inner border border-gray-200 map-container"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import L from 'leaflet';

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
export interface AccidentPoint {
  id: string;
  lat: number;
  lng: number;
  riskLevel: string;
  intersection: string;
  district?: string;
  incidents: number;
  commonCause?: string;
  mainVictims?: string[];
}

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

  // Limpiar marcadores anteriores
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

// --- Lifecycle ---
onMounted(() => {
  if (!mapContainer.value) return;

  map = L.map(mapContainer.value, {
    zoomControl: false,
  }).setView([props.centerLat, props.centerLng], props.zoom);

  // Control de zoom en la esquina inferior derecha
  L.control.zoom({ position: 'bottomright' }).addTo(map);

  // Tiles: Carto Light (fondo claro para resaltar los puntos)
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
</script>

<style scoped>
.map-container {
  min-height: 400px;
}
</style>
