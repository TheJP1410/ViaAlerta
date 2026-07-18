<template>
  <div 
    class="bg-white dark:bg-gray-800 rounded-t-3xl shadow-[0_-10px_40px_rgba(0,0,0,0.08)] dark:shadow-[0_-10px_40px_rgba(0,0,0,0.3)] z-20 absolute bottom-0 w-full transition-transform duration-300 touch-pan-y"
    :style="{ transform: `translateY(${translateY}px)` }"
    @touchstart="onTouchStart"
    @touchmove="onTouchMove"
    @touchend="onTouchEnd"
  >
    <!-- Drag handle -->
    <div 
      class="w-12 h-1.5 bg-gray-200 dark:bg-gray-600 rounded-full mx-auto mt-3 mb-3 cursor-pointer"
      @click="toggleState"
    ></div>

    <div class="px-6 pb-8">

      <!-- Si hay un punto seleccionado, mostrar sus detalles -->
      <div v-if="selectedPoint" class="mb-4">
        <div class="flex justify-between items-start mb-3">
          <h2 class="text-gray-900 dark:text-white font-bold text-lg leading-tight">{{ selectedPoint.intersection }}</h2>
          <span
            class="text-xs font-bold px-2.5 py-1 rounded-full whitespace-nowrap ml-2"
            :class="riskBadgeClasses"
          >
            {{ selectedPoint.riskLevel }}
          </span>
        </div>

        <!-- Detalle del punto -->
        <div class="grid grid-cols-3 gap-3 mb-4">
          <div class="bg-gray-50 dark:bg-gray-700 rounded-xl p-3 text-center">
            <p class="text-lg font-bold text-gray-900 dark:text-white">{{ selectedPoint.incidents }}</p>
            <p class="text-[10px] text-gray-500 dark:text-gray-400 font-medium uppercase tracking-wider">Casos</p>
          </div>
          <div class="bg-gray-50 dark:bg-gray-700 rounded-xl p-3 text-center">
            <p class="text-xs font-semibold text-gray-900 dark:text-white leading-tight">{{ selectedPoint.district }}</p>
            <p class="text-[10px] text-gray-500 dark:text-gray-400 font-medium uppercase tracking-wider mt-1">Distrito</p>
          </div>
          <div class="bg-gray-50 dark:bg-gray-700 rounded-xl p-3 text-center">
            <p class="text-xs font-semibold text-gray-900 dark:text-white leading-tight">{{ primaryVictim }}</p>
            <p class="text-[10px] text-gray-500 dark:text-gray-400 font-medium uppercase tracking-wider mt-1">Víctimas</p>
          </div>
        </div>

        <!-- Causa común -->
        <div v-if="selectedPoint.commonCause" class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-300 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400 dark:text-gray-500 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>Causa principal: <strong>{{ selectedPoint.commonCause }}</strong></span>
        </div>
      </div>

      <!-- Si NO hay punto seleccionado, mostrar resumen general -->
      <div v-else class="mb-4">
        <h2 class="text-gray-900 dark:text-white font-bold text-lg mb-1">Tu ruta actual</h2>
        <p class="text-sm text-gray-500 dark:text-gray-400">San Isidro, Centro Financiero</p>
      </div>

      <!-- Warning de puntos en ruta -->
      <div v-if="criticalCount > 0" class="bg-orange-50 dark:bg-orange-900/20 rounded-xl p-4 border border-orange-100 dark:border-orange-800 flex gap-3 items-center">
        <div class="text-orange-500 dark:text-orange-400 flex-shrink-0">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
        <p class="text-sm text-orange-800 dark:text-orange-300 font-medium leading-tight">
          Tu zona tiene <strong class="font-bold">{{ criticalCount }} punto{{ criticalCount > 1 ? 's' : '' }} crítico{{ criticalCount > 1 ? 's' : '' }}</strong> con <strong>{{ totalIncidents }}</strong> casos registrados para {{ userType }}.
        </p>
      </div>

      <!-- Ruta segura -->
      <div v-else class="bg-green-50 dark:bg-green-900/20 rounded-xl p-4 border border-green-100 dark:border-green-800 flex gap-3 items-center">
        <div class="text-green-500 dark:text-green-400 flex-shrink-0">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <p class="text-sm text-green-800 dark:text-green-300 font-medium leading-tight">
          Tu ruta actual no tiene puntos críticos reportados. ¡Buen viaje!
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import type { AccidentPoint } from '../services/api';

const props = withDefaults(
  defineProps<{
    selectedPoint?: AccidentPoint | null;
    criticalCount?: number;
    totalIncidents?: number;
    userType?: string;
  }>(),
  {
    selectedPoint: null,
    criticalCount: 0,
    totalIncidents: 0,
    userType: 'ciclistas',
  }
);

const primaryVictim = computed(() => {
  if (!props.selectedPoint?.mainVictims?.length) return '—';
  return props.selectedPoint.mainVictims[0];
});

const riskBadgeClasses = computed(() => {
  const level = props.selectedPoint?.riskLevel;
  if (level === 'Crítico') return 'bg-red-100 text-red-800 dark:bg-red-900/40 dark:text-red-300';
  if (level === 'Alto') return 'bg-red-50 text-red-600 dark:bg-red-900/20 dark:text-red-400';
  if (level === 'Medio') return 'bg-orange-50 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400';
  return 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300';
});

// --- Swipe / Drag Logic ---
const translateY = ref(0); // 0 = expandido, > 0 = colapsado
const isCollapsed = ref(false);
const COLLAPSED_Y = 180; // Píxeles hacia abajo cuando está colapsado

let startY = 0;
let currentY = 0;

function toggleState() {
  isCollapsed.value = !isCollapsed.value;
  translateY.value = isCollapsed.value ? COLLAPSED_Y : 0;
}

function onTouchStart(e: TouchEvent) {
  startY = e.touches[0].clientY;
}

function onTouchMove(e: TouchEvent) {
  const y = e.touches[0].clientY;
  const deltaY = y - startY;
  
  // Si estaba colapsado y arrastra hacia abajo, no ir más allá
  if (isCollapsed.value && deltaY > 0) return;
  // Si estaba expandido y arrastra hacia arriba, no ir más allá
  if (!isCollapsed.value && deltaY < 0) return;

  currentY = isCollapsed.value ? COLLAPSED_Y + deltaY : deltaY;
  
  // Limitar los valores
  if (currentY < 0) currentY = 0;
  if (currentY > COLLAPSED_Y + 20) currentY = COLLAPSED_Y + 20;
  
  translateY.value = currentY;
}

function onTouchEnd() {
  if (currentY > COLLAPSED_Y / 2) {
    isCollapsed.value = true;
    translateY.value = COLLAPSED_Y;
  } else {
    isCollapsed.value = false;
    translateY.value = 0;
  }
}

// Expandir automáticamente si seleccionan un punto
watch(() => props.selectedPoint, (newVal) => {
  if (newVal && isCollapsed.value) {
    isCollapsed.value = false;
    translateY.value = 0;
  }
});
</script>
