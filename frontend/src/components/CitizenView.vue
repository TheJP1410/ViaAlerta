<template>
  <main class="relative h-screen w-full flex flex-col bg-gray-50 overflow-hidden">

    <!-- Header -->
    <header class="bg-white px-5 py-4 shadow-sm z-10 flex justify-between items-center relative">
      <div class="flex items-center gap-2">
        <div class="w-8 h-8 rounded-full bg-red-500 flex items-center justify-center text-white">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 1.944A11.954 11.954 0 012.166 5C2.056 5.649 2 6.319 2 7c0 5.225 3.34 9.67 8 11.317C14.66 16.67 18 12.225 18 7c0-.682-.057-1.35-.166-1.998A11.954 11.954 0 0110 1.944zM11 14a1 1 0 11-2 0 1 1 0 012 0zm0-7a1 1 0 10-2 0v3a1 1 0 102 0V7z" clip-rule="evenodd" />
          </svg>
        </div>
        <h1 class="font-bold text-xl tracking-tight text-gray-900">VíaAlerta</h1>
      </div>
      <a href="/dashboard" class="p-2 bg-gray-100 rounded-full text-gray-600 hover:bg-gray-200 transition" title="Panel de Autoridades">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
        </svg>
      </a>
    </header>

    <!-- Alerta Predictiva: reacciona al punto seleccionado -->
    <AlertBanner
      :visible="showAlert"
      :risk-level="selectedPoint?.riskLevel ?? 'Alto'"
      :intersection="selectedPoint?.intersection ?? ''"
      :cause="selectedPoint?.commonCause"
      :distance="alertDistance"
      @dismiss="showAlert = false"
    />

    <!-- Mapa -->
    <div class="flex-grow relative z-0">
      <div class="absolute top-0 w-full h-8 bg-gradient-to-b from-black/5 to-transparent z-10 pointer-events-none"></div>

      <MapComponent
        :points="points"
        :center-lat="selectedPoint?.lat ?? -12.046374"
        :center-lng="selectedPoint?.lng ?? -77.042793"
        :zoom="14"
        @marker-click="onMarkerClick"
      />

      <!-- Locate Me FAB -->
      <button class="absolute bottom-32 right-4 bg-white p-3 rounded-full shadow-lg z-20 border border-gray-100 text-blue-600 hover:bg-gray-50 transition active:scale-95">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
        </svg>
      </button>
    </div>

    <!-- Bottom Sheet: muestra detalles del punto seleccionado -->
    <BottomSheet
      :selected-point="selectedPoint"
      :critical-count="criticalCount"
      :total-incidents="totalIncidents"
      user-type="ciclistas"
    />

  </main>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import MapComponent from './Map.vue';
import AlertBanner from './AlertBanner.vue';
import BottomSheet from './BottomSheet.vue';
import type { AccidentPoint } from '../services/api';

const props = defineProps<{
  points: AccidentPoint[];
}>();

// Estado reactivo: punto actualmente seleccionado (inicia con el más peligroso)
const selectedPoint = ref<AccidentPoint | null>(
  props.points.length > 0
    ? [...props.points].sort((a, b) => b.incidents - a.incidents)[0]
    : null
);

const showAlert = ref(true);

// Distancias simuladas para demostración
const distances = ['200m', '350m', '500m', '800m', '1.2km'];
const alertDistance = ref('200m');

// Métricas computadas
const criticalCount = computed(() =>
  props.points.filter(p => p.riskLevel === 'Alto' || p.riskLevel === 'Crítico').length
);

const totalIncidents = computed(() =>
  props.points.reduce((acc, p) => acc + p.incidents, 0)
);

// Cuando el usuario toca un marcador en el mapa
function onMarkerClick(point: AccidentPoint) {
  selectedPoint.value = point;
  showAlert.value = true;
  // Simular distancia aleatoria
  alertDistance.value = distances[Math.floor(Math.random() * distances.length)];
}
</script>
