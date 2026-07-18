<template>
  <main class="relative h-screen w-full flex flex-col bg-gray-50 dark:bg-gray-900 overflow-hidden">

    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 px-5 py-4 shadow-sm z-10 flex justify-between items-center relative transition-colors">
      <div class="flex items-center gap-2">
        <div class="w-8 h-8 rounded-full bg-red-500 flex items-center justify-center text-white">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 1.944A11.954 11.954 0 012.166 5C2.056 5.649 2 6.319 2 7c0 5.225 3.34 9.67 8 11.317C14.66 16.67 18 12.225 18 7c0-.682-.057-1.35-.166-1.998A11.954 11.954 0 0110 1.944zM11 14a1 1 0 11-2 0 1 1 0 012 0zm0-7a1 1 0 10-2 0v3a1 1 0 102 0V7z" clip-rule="evenodd" />
          </svg>
        </div>
        <h1 class="font-bold text-xl tracking-tight text-gray-900 dark:text-white">VíaAlerta</h1>
      </div>
      
      <!-- Botón Menú Hamburguesa -->
      <button 
        @click="isMenuOpen = true"
        class="p-2 bg-gray-100 dark:bg-gray-700 rounded-full text-gray-600 dark:text-gray-200 hover:bg-gray-200 dark:hover:bg-gray-600 transition" 
        title="Menú Principal"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
        </svg>
      </button>
    </header>

    <!-- Drawer Overlay -->
    <div 
      v-if="isMenuOpen" 
      class="fixed inset-0 bg-black/50 z-40 transition-opacity"
      @click="isMenuOpen = false"
    ></div>

    <!-- Drawer Menú -->
    <div 
      class="fixed top-0 right-0 h-full w-72 bg-white dark:bg-gray-800 shadow-2xl z-50 transform transition-transform duration-300 ease-in-out flex flex-col"
      :class="isMenuOpen ? 'translate-x-0' : 'translate-x-full'"
    >
      <div class="p-5 border-b border-gray-100 dark:border-gray-700 flex justify-between items-center">
        <h2 class="font-bold text-lg text-gray-900 dark:text-white">Menú</h2>
        <button @click="isMenuOpen = false" class="text-gray-500 dark:text-gray-400 p-1">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <div class="p-5 flex-grow flex flex-col gap-4">
        <!-- Toggle Modo Oscuro -->
        <button 
          @click="toggleDarkMode" 
          class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-xl w-full text-left transition"
        >
          <div class="flex items-center gap-3 text-gray-700 dark:text-gray-200 font-medium">
            <svg v-if="isDark" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-yellow-400" viewBox="0 0 20 20" fill="currentColor">
              <path d="M17.293 13.293A8 8 0 016.707 2.707a8.001 8.001 0 1010.586 10.586z" />
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
            Modo Oscuro
          </div>
          <!-- Switch -->
          <div class="w-10 h-6 bg-gray-300 dark:bg-blue-600 rounded-full relative transition-colors">
            <div 
              class="w-4 h-4 bg-white rounded-full absolute top-1 transition-transform"
              :class="isDark ? 'right-1' : 'left-1'"
            ></div>
          </div>
        </button>

        <!-- Enlace Dashboard -->
        <a 
          href="/dashboard" 
          class="flex items-center gap-3 p-3 text-gray-700 dark:text-gray-200 font-medium bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 rounded-xl transition"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600 dark:text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          Acceso Autoridades
        </a>
      </div>
      
      <div class="p-5 text-center text-xs text-gray-400 dark:text-gray-500">
        VíaAlerta v1.0.0
      </div>
    </div>

    <!-- Alerta Predictiva -->
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
        ref="mapRef"
        :points="points"
        :center-lat="mapCenter.lat"
        :center-lng="mapCenter.lng"
        :zoom="14"
        @marker-click="onMarkerClick"
      />

      <!-- Locate Me FAB -->
      <button
        class="absolute bottom-32 right-4 dark:bg-gray-800 bg-white p-3 rounded-full shadow-lg z-20 border border-gray-100 dark:border-gray-700 transition active:scale-95"
        :class="locating ? 'text-blue-400 animate-pulse' : locateError ? 'text-red-400 animate-shake' : 'text-blue-600 dark:text-blue-400 hover:bg-gray-50 dark:hover:bg-gray-700'"
        @click="handleLocate"
        :disabled="locating"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
        </svg>
      </button>
    </div>

    <!-- Bottom Sheet -->
    <BottomSheet
      :selected-point="selectedPoint"
      :critical-count="criticalCount"
      :total-incidents="totalIncidents"
      user-type="ciclistas"
    />

  </main>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue';
import MapComponent from './Map.vue';
import AlertBanner from './AlertBanner.vue';
import BottomSheet from './BottomSheet.vue';
import type { AccidentPoint } from '../services/api';
import { useDarkMode } from '../composables/useDarkMode';

const props = defineProps<{
  points: AccidentPoint[];
}>();

const { isDark, toggleDarkMode } = useDarkMode();

// Ref al componente Map para llamar sus métodos expuestos
const mapRef = ref<InstanceType<typeof MapComponent> | null>(null);

// Estado reactivo
const isMenuOpen = ref(false);
const selectedPoint = ref<AccidentPoint | null>(
  props.points.length > 0
    ? [...props.points].sort((a, b) => b.incidents - a.incidents)[0]
    : null
);

const showAlert = ref(true);
const locating = ref(false);
const locateError = ref(false);

// Centro del mapa reactivo
const mapCenter = reactive({
  lat: selectedPoint.value?.lat ?? -12.046374,
  lng: selectedPoint.value?.lng ?? -77.042793,
});

// Distancias simuladas
const distances = ['200m', '350m', '500m', '800m', '1.2km'];
const alertDistance = ref('200m');

// Métricas computadas
const criticalCount = computed(() =>
  props.points.filter(p => p.riskLevel === 'Alto' || p.riskLevel === 'Crítico').length
);

const totalIncidents = computed(() =>
  props.points.reduce((acc, p) => acc + p.incidents, 0)
);

// Click en marcador del mapa
function onMarkerClick(point: AccidentPoint) {
  selectedPoint.value = point;
  showAlert.value = true;
  alertDistance.value = distances[Math.floor(Math.random() * distances.length)];

  // Volar hacia el punto seleccionado
  mapCenter.lat = point.lat;
  mapCenter.lng = point.lng;
}

// Botón "Mi Ubicación"
async function handleLocate() {
  if (!mapRef.value) return;
  locating.value = true;
  locateError.value = false;

  try {
    await mapRef.value.locateUser();
  } catch {
    locateError.value = true;
    // Quitar el estado de error tras 1.5s
    setTimeout(() => { locateError.value = false; }, 1500);
  } finally {
    locating.value = false;
  }
}
</script>

<style>
/* Animación shake para cuando falla la geolocalización */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20% { transform: translateX(-4px); }
  40% { transform: translateX(4px); }
  60% { transform: translateX(-3px); }
  80% { transform: translateX(3px); }
}
.animate-shake {
  animation: shake 0.4s ease-in-out;
}
</style>
