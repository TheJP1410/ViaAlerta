<template>
  <Transition name="slide-down">
    <div v-if="visible" class="absolute top-20 left-0 w-full px-4 z-20 pointer-events-none">
      <div
        class="text-white p-4 rounded-2xl shadow-xl flex items-start gap-4 pointer-events-auto border-2 cursor-pointer"
        :class="bgClasses"
        @click="$emit('dismiss')"
      >
        <!-- Icono de alerta -->
        <div class="mt-1 flex-shrink-0">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 animate-pulse" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>

        <!-- Texto -->
        <div class="min-w-0">
          <p class="font-bold text-lg leading-tight mb-1">¡Cuidado! Zona {{ riskLabel }} a {{ distance }}</p>
          <p class="text-sm leading-snug opacity-90">{{ intersection }}</p>
          <p v-if="cause" class="text-sm leading-snug font-medium mt-1 opacity-80">Causa común: {{ cause }}</p>
        </div>

        <!-- Botón cerrar -->
        <button class="flex-shrink-0 mt-0.5 opacity-70 hover:opacity-100 transition pointer-events-auto" @click.stop="$emit('dismiss')">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(
  defineProps<{
    visible?: boolean;
    riskLevel?: string;
    intersection: string;
    cause?: string;
    distance?: string;
  }>(),
  {
    visible: true,
    riskLevel: 'Alto',
    distance: '200m',
  }
);

defineEmits<{
  (e: 'dismiss'): void;
}>();

const riskLabel = computed(() => {
  if (props.riskLevel === 'Crítico') return 'Crítica';
  if (props.riskLevel === 'Alto') return 'de Alto Riesgo';
  return 'de Riesgo';
});

const bgClasses = computed(() => {
  if (props.riskLevel === 'Crítico') return 'bg-red-800 border-red-700';
  if (props.riskLevel === 'Alto') return 'bg-red-500 border-red-400';
  return 'bg-orange-500 border-orange-400';
});
</script>

<style scoped>
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.35s ease;
}
.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
