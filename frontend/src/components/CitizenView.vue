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

    <!-- Alerta Predictiva -->
    <AlertBanner
      :visible="showAlert"
      :risk-level="nearestDanger?.riskLevel"
      :intersection="nearestDanger?.intersection ?? ''"
      :cause="nearestDanger?.commonCause"
      distance="200m"
      @dismiss="showAlert = false"
    />

    <!-- Mapa -->
    <div class="flex-grow relative z-0">
      <div class="absolute top-0 w-full h-8 bg-gradient-to-b from-black/5 to-transparent z-10 pointer-events-none"></div>

      <MapComponent
        :points="points"
        :center-lat="nearestDanger?.lat ?? -12.046374"
        :center-lng="nearestDanger?.lng ?? -77.042793"
        :zoom="15"
        @marker-click="onMarkerClick"
      />

      <!-- Locate Me FAB -->
      <button class="absolute bottom-32 right-4 bg-white p-3 rounded-full shadow-lg z-20 border border-gray-100 text-blue-600 hover:bg-gray-50 transition">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
        </svg>
      </button>
    </div>

    <!-- Bottom Sheet -->
    <BottomSheet
      :critical-count="criticalCount"
      user-type="ciclistas"
    />

  </main>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import MapComponent from './Map.vue';
import AlertBanner from './AlertBanner.vue';
import BottomSheet from './BottomSheet.vue';
import type { AccidentPoint } from './Map.vue';

const props = defineProps<{
  points: AccidentPoint[];
}>();

const showAlert = ref(true);

// El punto más peligroso cercano (simulado: el primero del array)
const nearestDanger = computed(() => props.points[0] ?? null);

// Puntos críticos en la "ruta" (simulamos que son los de riesgo Alto o Crítico)
const criticalCount = computed(() =>
  props.points.filter(p => p.riskLevel === 'Alto' || p.riskLevel === 'Crítico').length
);

function onMarkerClick(point: AccidentPoint) {
  showAlert.value = true;
  // Aquí podríamos actualizar el nearestDanger con el punto clickeado
  console.log('Punto seleccionado:', point.intersection);
}
</script>
