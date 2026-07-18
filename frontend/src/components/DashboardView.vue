<template>
  <div class="h-screen w-full flex bg-gray-100 dark:bg-gray-900 overflow-hidden">

    <!-- Mobile overlay para el sidebar -->
    <div 
      v-if="isSidebarOpen" 
      class="fixed inset-0 bg-black/50 z-30 lg:hidden transition-opacity"
      @click="isSidebarOpen = false"
    ></div>

    <!-- Sidebar Izquierdo -->
    <aside 
      class="w-96 flex-shrink-0 bg-white dark:bg-gray-800 shadow-xl z-40 flex flex-col h-full border-r border-gray-200 dark:border-gray-700 absolute lg:relative transform transition-transform duration-300"
      :class="isSidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'"
    >
      <!-- Logo y Cabecera -->
      <div class="p-6 border-b border-gray-100 dark:border-gray-700 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-lg bg-blue-900 dark:bg-blue-700 flex items-center justify-center text-white">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <div>
            <h1 class="font-bold text-xl text-gray-900 dark:text-white leading-tight">VíaAlerta</h1>
            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium tracking-wide uppercase">Centro de Control</p>
          </div>
        </div>
        <!-- Botón para ocultar sidebar en móvil -->
        <button 
          @click="isSidebarOpen = false" 
          class="lg:hidden p-2 text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
      </div>

      <!-- Filtros -->
      <div class="px-6 pt-4 pb-2 flex gap-2">
        <button
          v-for="f in filters"
          :key="f.value"
          class="text-xs font-semibold px-3 py-1.5 rounded-full border transition"
          :class="activeFilter === f.value
            ? 'bg-blue-900 dark:bg-blue-600 text-white border-blue-900 dark:border-blue-600'
            : 'bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-300 border-gray-200 dark:border-gray-600 hover:border-gray-400 dark:hover:border-gray-400'"
          @click="activeFilter = f.value"
        >
          {{ f.label }}
        </button>
      </div>

      <!-- Contenido scrollable -->
      <div class="flex-grow overflow-y-auto p-6 pt-4">

        <!-- KPIs -->
        <div class="grid grid-cols-3 gap-3 mb-6">
          <div class="bg-blue-50 dark:bg-blue-900/30 rounded-xl p-3 border border-blue-100 dark:border-blue-800 text-center">
            <p class="text-2xl font-bold text-blue-900 dark:text-blue-200">{{ totalIncidents }}</p>
            <p class="text-[10px] text-blue-600 dark:text-blue-400 font-semibold uppercase tracking-wider">Accidentes</p>
          </div>
          <div class="bg-red-50 dark:bg-red-900/30 rounded-xl p-3 border border-red-100 dark:border-red-800 text-center">
            <p class="text-2xl font-bold text-red-900 dark:text-red-200">{{ criticalPointsCount }}</p>
            <p class="text-[10px] text-red-600 dark:text-red-400 font-semibold uppercase tracking-wider">Críticos</p>
          </div>
          <div class="bg-amber-50 dark:bg-amber-900/30 rounded-xl p-3 border border-amber-100 dark:border-amber-800 text-center">
            <p class="text-2xl font-bold text-amber-900 dark:text-amber-200">{{ filteredPoints.length }}</p>
            <p class="text-[10px] text-amber-600 dark:text-amber-400 font-semibold uppercase tracking-wider">Zonas</p>
          </div>
        </div>

        <!-- Lista de Cruces Peligrosos -->
        <div>
          <div class="flex justify-between items-end mb-4">
            <h2 class="font-bold text-gray-900 dark:text-white text-base">Top Zonas de Riesgo</h2>
          </div>

          <!-- Empty State -->
          <div v-if="sortedPoints.length === 0" class="text-center py-10">
            <div class="w-16 h-16 mx-auto mb-4 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-300 dark:text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
              </svg>
            </div>
            <p class="text-sm font-semibold text-gray-500 dark:text-gray-400 mb-1">Sin resultados</p>
            <p class="text-xs text-gray-400 dark:text-gray-500">No se encontraron zonas con estos filtros.</p>
            <button
              class="mt-3 text-xs text-blue-600 dark:text-blue-400 font-medium hover:underline"
              @click="activeFilter = 'todos'; searchQuery = ''"
            >
              Limpiar filtros
            </button>
          </div>

          <!-- Lista -->
          <div v-else class="flex flex-col gap-2.5">
            <div
              v-for="(punto, index) in sortedPoints"
              :key="punto.id"
              class="border rounded-xl p-4 transition cursor-pointer flex gap-4 items-center"
              :class="selectedPoint?.id === punto.id
                ? 'bg-blue-50 dark:bg-blue-900/30 border-blue-300 dark:border-blue-700 shadow-md'
                : 'bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700 hover:shadow-md'"
              @click="selectPoint(punto)"
            >
              <!-- Ranking badge -->
              <div
                class="w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm text-white flex-shrink-0"
                :class="getRiskBgClass(punto.riskLevel)"
              >
                {{ index + 1 }}
              </div>

              <!-- Info -->
              <div class="flex-grow min-w-0">
                <h3 class="text-sm font-bold text-gray-900 dark:text-white leading-tight mb-0.5 truncate">{{ punto.intersection }}</h3>
                <p class="text-xs text-gray-500 dark:text-gray-400">{{ punto.district }}</p>
              </div>

              <!-- Casos -->
              <div class="text-right flex-shrink-0">
                <p class="text-lg font-bold text-gray-900 dark:text-white">{{ punto.incidents }}</p>
                <p class="text-[10px] text-gray-400 dark:text-gray-500 font-medium uppercase tracking-wider">Casos</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer Sidebar -->
      <div class="p-4 border-t border-gray-100 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-center flex flex-col gap-3">
        <a href="/" class="text-xs text-blue-600 dark:text-blue-400 font-semibold hover:underline">← Volver a Inicio</a>
        <p class="text-[10px] text-gray-500 dark:text-gray-500 font-medium">Última actualización: {{ lastUpdate }}</p>
      </div>
    </aside>

    <!-- Área Principal: Mapa + Detalle -->
    <main class="flex-grow relative z-0">

      <!-- Controles Superiores: Menú Móvil + Barra de Búsqueda -->
      <div class="absolute top-6 left-6 right-6 lg:right-auto z-20 flex gap-3 lg:w-full lg:max-w-sm">
        <!-- Botón menú hamburguesa (solo móvil) -->
        <button 
          @click="isSidebarOpen = true"
          class="lg:hidden bg-white dark:bg-gray-800 rounded-xl shadow-lg p-3 border border-gray-100 dark:border-gray-700 text-gray-600 dark:text-gray-300"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>

        <!-- Barra de búsqueda -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-lg flex-grow flex items-center px-4 py-3 border border-gray-100 dark:border-gray-700">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400 dark:text-gray-500 mr-3 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            v-model="searchQuery"
            @input="onSearch"
            type="text"
            placeholder="Buscar distrito o cruce..."
            class="w-full outline-none text-gray-700 dark:text-gray-200 bg-transparent placeholder-gray-400 dark:placeholder-gray-500 text-sm"
          />
        </div>
      </div>

      <!-- Tarjeta de detalle (aparece al seleccionar un punto) -->
      <Transition name="slide-up">
        <div v-if="selectedPoint" class="absolute bottom-6 left-6 right-6 lg:right-auto z-20 lg:w-96">
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl border border-gray-100 dark:border-gray-700 p-5">
            <div class="flex justify-between items-start mb-3">
              <div class="min-w-0 mr-3">
                <h3 class="font-bold text-gray-900 dark:text-white text-base leading-tight">{{ selectedPoint.intersection }}</h3>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{{ selectedPoint.district }}</p>
              </div>
              <span
                class="text-xs font-bold px-2.5 py-1 rounded-full whitespace-nowrap"
                :class="getRiskBadgeClasses(selectedPoint.riskLevel)"
              >
                {{ selectedPoint.riskLevel }}
              </span>
            </div>

            <div class="grid grid-cols-3 gap-3 mb-3">
              <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-2.5 text-center">
                <p class="text-xl font-bold text-gray-900 dark:text-white">{{ selectedPoint.incidents }}</p>
                <p class="text-[10px] text-gray-500 dark:text-gray-400 font-medium uppercase">Casos</p>
              </div>
              <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-2.5 text-center">
                <p class="text-xs font-semibold text-gray-900 dark:text-white">{{ selectedPoint.commonCause }}</p>
                <p class="text-[10px] text-gray-500 dark:text-gray-400 font-medium uppercase mt-0.5">Causa</p>
              </div>
              <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-2.5 text-center">
                <p class="text-xs font-semibold text-gray-900 dark:text-white">{{ primaryVictim }}</p>
                <p class="text-[10px] text-gray-500 dark:text-gray-400 font-medium uppercase mt-0.5">Víctimas</p>
              </div>
            </div>

            <button
              class="w-full text-center text-xs text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 transition font-medium pt-1"
              @click="selectedPoint = null"
            >
              Cerrar detalle
            </button>
          </div>
        </div>
      </Transition>

      <!-- Mapa -->
      <MapComponent
        ref="mapRef"
        :points="filteredPoints"
        :center-lat="mapCenter.lat"
        :center-lng="mapCenter.lng"
        :zoom="12"
        @marker-click="selectPoint"
      />
      <!-- Locate Me FAB (Dashboard) -->
      <button
        class="absolute bottom-6 right-6 dark:bg-gray-800 bg-white p-3 rounded-full shadow-lg z-20 border border-gray-100 dark:border-gray-700 transition active:scale-95"
        :class="locating ? 'text-blue-400 animate-pulse' : locateError ? 'text-red-400 animate-shake' : 'text-blue-600 dark:text-blue-400 hover:bg-gray-50 dark:hover:bg-gray-700'"
        @click="handleLocate"
        :disabled="locating"
        title="Centrar en mi ubicación"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-6 h-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
        </svg>
      </button>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue';
import MapComponent from './Map.vue';
import type { AccidentPoint } from '../services/api';

const props = defineProps<{
  points: AccidentPoint[];
}>();

// Ref al mapa
const mapRef = ref<InstanceType<typeof MapComponent> | null>(null);

// --- Estado ---
const isSidebarOpen = ref(true);
const selectedPoint = ref<AccidentPoint | null>(null);
const searchQuery = ref('');
const activeFilter = ref('todos');
const locating = ref(false);
const locateError = ref(false);

const mapCenter = reactive({
  lat: -12.06,
  lng: -77.03,
});

const filters = [
  { label: 'Todos', value: 'todos' },
  { label: 'Crítico', value: 'Crítico' },
  { label: 'Alto', value: 'Alto' },
  { label: 'Medio', value: 'Medio' },
];

// --- Computed ---
const filteredPoints = computed(() => {
  let result = props.points;

  if (activeFilter.value !== 'todos') {
    result = result.filter(p => p.riskLevel === activeFilter.value);
  }

  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(p =>
      p.intersection.toLowerCase().includes(q) ||
      p.district.toLowerCase().includes(q)
    );
  }

  return result;
});

const sortedPoints = computed(() =>
  [...filteredPoints.value].sort((a, b) => b.incidents - a.incidents)
);

const totalIncidents = computed(() =>
  props.points.reduce((acc, p) => acc + p.incidents, 0)
);

const criticalPointsCount = computed(() =>
  props.points.filter(p => p.riskLevel === 'Crítico' || p.riskLevel === 'Alto').length
);

const primaryVictim = computed(() => {
  if (!selectedPoint.value?.mainVictims?.length) return '—';
  return selectedPoint.value.mainVictims[0];
});

const lastUpdate = computed(() => {
  const now = new Date();
  const hours = now.getHours().toString().padStart(2, '0');
  const mins = now.getMinutes().toString().padStart(2, '0');
  return `Hoy, ${hours}:${mins} hrs`;
});

// --- Acciones ---
function selectPoint(point: AccidentPoint) {
  selectedPoint.value = point;
  // Volar hacia el punto seleccionado
  mapCenter.lat = point.lat;
  mapCenter.lng = point.lng;
  // En móvil, colapsar sidebar para ver el mapa
  if (window.innerWidth < 1024) {
    isSidebarOpen.value = false;
  }
}

// Búsqueda: si hay un solo punto, o calcular el promedio, y centrar mapa
function onSearch() {
  if (filteredPoints.value.length > 0 && searchQuery.value.trim().length > 2) {
    // Calculamos el centroide de los resultados
    const lats = filteredPoints.value.map(p => p.lat);
    const lngs = filteredPoints.value.map(p => p.lng);
    const avgLat = lats.reduce((a, b) => a + b, 0) / lats.length;
    const avgLng = lngs.reduce((a, b) => a + b, 0) / lngs.length;
    mapCenter.lat = avgLat;
    mapCenter.lng = avgLng;
  }
}

async function handleLocate() {
  if (!mapRef.value) return;
  locating.value = true;
  locateError.value = false;
  try {
    await mapRef.value.locateUser();
  } catch {
    locateError.value = true;
    setTimeout(() => { locateError.value = false; }, 1500);
  } finally {
    locating.value = false;
  }
}

function getRiskBgClass(level: string) {
  if (level === 'Crítico') return 'bg-red-800';
  if (level === 'Alto') return 'bg-red-500';
  return 'bg-orange-500';
}

function getRiskBadgeClasses(level: string) {
  if (level === 'Crítico') return 'bg-red-100 text-red-800 dark:bg-red-900/40 dark:text-red-300';
  if (level === 'Alto') return 'bg-red-50 text-red-600 dark:bg-red-900/20 dark:text-red-400';
  return 'bg-orange-50 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400';
}
</script>

<style scoped>
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
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}
.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
