<template>
  <div class="h-screen w-full flex bg-gray-100 overflow-hidden">

    <!-- Sidebar Izquierdo -->
    <aside class="w-96 flex-shrink-0 bg-white shadow-xl z-20 flex flex-col h-full border-r border-gray-200">

      <!-- Logo y Cabecera -->
      <div class="p-6 border-b border-gray-100 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-lg bg-blue-900 flex items-center justify-center text-white">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <div>
            <h1 class="font-bold text-xl text-gray-900 leading-tight">VíaAlerta</h1>
            <p class="text-xs text-gray-500 font-medium tracking-wide uppercase">Centro de Control</p>
          </div>
        </div>
        <a href="/" class="p-2 bg-gray-100 rounded-full text-gray-500 hover:bg-gray-200 transition" title="Vista Ciudadano">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
        </a>
      </div>

      <!-- Filtros -->
      <div class="px-6 pt-4 pb-2 flex gap-2">
        <button
          v-for="f in filters"
          :key="f.value"
          class="text-xs font-semibold px-3 py-1.5 rounded-full border transition"
          :class="activeFilter === f.value
            ? 'bg-blue-900 text-white border-blue-900'
            : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'"
          @click="activeFilter = f.value"
        >
          {{ f.label }}
        </button>
      </div>

      <!-- Contenido scrollable -->
      <div class="flex-grow overflow-y-auto p-6 pt-4">

        <!-- KPIs -->
        <div class="grid grid-cols-3 gap-3 mb-6">
          <div class="bg-blue-50 rounded-xl p-3 border border-blue-100 text-center">
            <p class="text-2xl font-bold text-blue-900">{{ totalIncidents }}</p>
            <p class="text-[10px] text-blue-600 font-semibold uppercase tracking-wider">Accidentes</p>
          </div>
          <div class="bg-red-50 rounded-xl p-3 border border-red-100 text-center">
            <p class="text-2xl font-bold text-red-900">{{ criticalPointsCount }}</p>
            <p class="text-[10px] text-red-600 font-semibold uppercase tracking-wider">Críticos</p>
          </div>
          <div class="bg-amber-50 rounded-xl p-3 border border-amber-100 text-center">
            <p class="text-2xl font-bold text-amber-900">{{ filteredPoints.length }}</p>
            <p class="text-[10px] text-amber-600 font-semibold uppercase tracking-wider">Zonas</p>
          </div>
        </div>

        <!-- Lista de Cruces Peligrosos -->
        <div>
          <div class="flex justify-between items-end mb-4">
            <h2 class="font-bold text-gray-900 text-base">Top Zonas de Riesgo</h2>
          </div>

          <!-- Empty State -->
          <div v-if="sortedPoints.length === 0" class="text-center py-10">
            <div class="w-16 h-16 mx-auto mb-4 bg-gray-100 rounded-full flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
              </svg>
            </div>
            <p class="text-sm font-semibold text-gray-500 mb-1">Sin resultados</p>
            <p class="text-xs text-gray-400">No se encontraron zonas con estos filtros.</p>
            <button
              class="mt-3 text-xs text-blue-600 font-medium hover:underline"
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
                ? 'bg-blue-50 border-blue-300 shadow-md'
                : 'bg-white border-gray-200 hover:shadow-md'"
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
                <h3 class="text-sm font-bold text-gray-900 leading-tight mb-0.5 truncate">{{ punto.intersection }}</h3>
                <p class="text-xs text-gray-500">{{ punto.district }}</p>
              </div>

              <!-- Casos -->
              <div class="text-right flex-shrink-0">
                <p class="text-lg font-bold text-gray-900">{{ punto.incidents }}</p>
                <p class="text-[10px] text-gray-400 font-medium uppercase tracking-wider">Casos</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer Sidebar -->
      <div class="p-4 border-t border-gray-100 bg-gray-50 text-center">
        <p class="text-xs text-gray-500 font-medium">Última actualización: {{ lastUpdate }}</p>
      </div>
    </aside>

    <!-- Área Principal: Mapa + Detalle -->
    <main class="flex-grow relative z-0">

      <!-- Barra de búsqueda -->
      <div class="absolute top-6 left-6 z-20 w-full max-w-sm">
        <div class="bg-white rounded-xl shadow-lg flex items-center px-4 py-3 border border-gray-100">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400 mr-3 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar distrito o cruce..."
            class="w-full outline-none text-gray-700 bg-transparent placeholder-gray-400 text-sm"
          />
        </div>
      </div>

      <!-- Tarjeta de detalle (aparece al seleccionar un punto) -->
      <Transition name="slide-up">
        <div v-if="selectedPoint" class="absolute bottom-6 left-6 z-20 w-96">
          <div class="bg-white rounded-2xl shadow-2xl border border-gray-100 p-5">
            <div class="flex justify-between items-start mb-3">
              <div class="min-w-0 mr-3">
                <h3 class="font-bold text-gray-900 text-base leading-tight">{{ selectedPoint.intersection }}</h3>
                <p class="text-xs text-gray-500 mt-0.5">{{ selectedPoint.district }}</p>
              </div>
              <span
                class="text-xs font-bold px-2.5 py-1 rounded-full whitespace-nowrap"
                :class="getRiskBadgeClasses(selectedPoint.riskLevel)"
              >
                {{ selectedPoint.riskLevel }}
              </span>
            </div>

            <div class="grid grid-cols-3 gap-3 mb-3">
              <div class="bg-gray-50 rounded-lg p-2.5 text-center">
                <p class="text-xl font-bold text-gray-900">{{ selectedPoint.incidents }}</p>
                <p class="text-[10px] text-gray-500 font-medium uppercase">Casos</p>
              </div>
              <div class="bg-gray-50 rounded-lg p-2.5 text-center">
                <p class="text-xs font-semibold text-gray-900">{{ selectedPoint.commonCause }}</p>
                <p class="text-[10px] text-gray-500 font-medium uppercase mt-0.5">Causa</p>
              </div>
              <div class="bg-gray-50 rounded-lg p-2.5 text-center">
                <p class="text-xs font-semibold text-gray-900">{{ primaryVictim }}</p>
                <p class="text-[10px] text-gray-500 font-medium uppercase mt-0.5">Víctimas</p>
              </div>
            </div>

            <button
              class="w-full text-center text-xs text-gray-400 hover:text-gray-600 transition font-medium pt-1"
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
const selectedPoint = ref<AccidentPoint | null>(null);
const searchQuery = ref('');
const activeFilter = ref('todos');

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
}

function getRiskBgClass(level: string) {
  if (level === 'Crítico') return 'bg-red-800';
  if (level === 'Alto') return 'bg-red-500';
  return 'bg-orange-500';
}

function getRiskBadgeClasses(level: string) {
  if (level === 'Crítico') return 'bg-red-100 text-red-800';
  if (level === 'Alto') return 'bg-red-50 text-red-600';
  return 'bg-orange-50 text-orange-700';
}
</script>

<style scoped>
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
