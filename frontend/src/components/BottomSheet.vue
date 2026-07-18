<template>
  <div class="bg-white rounded-t-3xl shadow-[0_-10px_40px_rgba(0,0,0,0.08)] z-20 absolute bottom-0 w-full transition-transform duration-300">
    <!-- Drag handle -->
    <div class="w-12 h-1.5 bg-gray-200 rounded-full mx-auto mt-3 mb-3"></div>

    <div class="px-6 pb-8">

      <!-- Si hay un punto seleccionado, mostrar sus detalles -->
      <div v-if="selectedPoint" class="mb-4">
        <div class="flex justify-between items-start mb-3">
          <h2 class="text-gray-900 font-bold text-lg leading-tight">{{ selectedPoint.intersection }}</h2>
          <span
            class="text-xs font-bold px-2.5 py-1 rounded-full whitespace-nowrap ml-2"
            :class="riskBadgeClasses"
          >
            {{ selectedPoint.riskLevel }}
          </span>
        </div>

        <!-- Detalle del punto -->
        <div class="grid grid-cols-3 gap-3 mb-4">
          <div class="bg-gray-50 rounded-xl p-3 text-center">
            <p class="text-lg font-bold text-gray-900">{{ selectedPoint.incidents }}</p>
            <p class="text-[10px] text-gray-500 font-medium uppercase tracking-wider">Casos</p>
          </div>
          <div class="bg-gray-50 rounded-xl p-3 text-center">
            <p class="text-xs font-semibold text-gray-900 leading-tight">{{ selectedPoint.district }}</p>
            <p class="text-[10px] text-gray-500 font-medium uppercase tracking-wider mt-1">Distrito</p>
          </div>
          <div class="bg-gray-50 rounded-xl p-3 text-center">
            <p class="text-xs font-semibold text-gray-900 leading-tight">{{ primaryVictim }}</p>
            <p class="text-[10px] text-gray-500 font-medium uppercase tracking-wider mt-1">Víctimas</p>
          </div>
        </div>

        <!-- Causa común -->
        <div v-if="selectedPoint.commonCause" class="flex items-center gap-2 text-sm text-gray-600 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-gray-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>Causa principal: <strong>{{ selectedPoint.commonCause }}</strong></span>
        </div>
      </div>

      <!-- Si NO hay punto seleccionado, mostrar resumen general -->
      <div v-else class="mb-4">
        <h2 class="text-gray-900 font-bold text-lg mb-1">Tu ruta actual</h2>
        <p class="text-sm text-gray-500">San Isidro, Centro Financiero</p>
      </div>

      <!-- Warning de puntos en ruta -->
      <div v-if="criticalCount > 0" class="bg-orange-50 rounded-xl p-4 border border-orange-100 flex gap-3 items-center">
        <div class="text-orange-500 flex-shrink-0">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
        <p class="text-sm text-orange-800 font-medium leading-tight">
          Tu zona tiene <strong class="font-bold">{{ criticalCount }} punto{{ criticalCount > 1 ? 's' : '' }} crítico{{ criticalCount > 1 ? 's' : '' }}</strong> con <strong>{{ totalIncidents }}</strong> casos registrados para {{ userType }}.
        </p>
      </div>

      <!-- Ruta segura -->
      <div v-else class="bg-green-50 rounded-xl p-4 border border-green-100 flex gap-3 items-center">
        <div class="text-green-500 flex-shrink-0">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <p class="text-sm text-green-800 font-medium leading-tight">
          Tu ruta actual no tiene puntos críticos reportados. ¡Buen viaje!
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
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
  if (level === 'Crítico') return 'bg-red-100 text-red-800';
  if (level === 'Alto') return 'bg-red-50 text-red-600';
  if (level === 'Medio') return 'bg-orange-50 text-orange-700';
  return 'bg-gray-100 text-gray-600';
});
</script>
