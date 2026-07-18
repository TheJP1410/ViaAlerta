<template>
  <div class="bg-white rounded-t-3xl shadow-[0_-10px_40px_rgba(0,0,0,0.08)] z-20 absolute bottom-0 w-full transition-transform duration-300">
    <!-- Drag handle -->
    <div class="w-12 h-1.5 bg-gray-200 rounded-full mx-auto mt-3 mb-4"></div>

    <div class="px-6 pb-8">
      <h2 class="text-gray-900 font-bold text-lg mb-4">Tu ruta actual</h2>

      <!-- Destino -->
      <div class="flex items-center gap-4 mb-4">
        <div class="w-10 h-10 rounded-full bg-blue-50 flex items-center justify-center text-blue-600">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-6 h-6">
            <path d="M3.478 2.405a.75.75 0 00-.926.94l2.432 7.905H13.5a.75.75 0 010 1.5H4.984l-2.432 7.905a.75.75 0 00.926.94 60.519 60.519 0 0018.445-8.986.75.75 0 000-1.218A60.517 60.517 0 003.478 2.405z" />
          </svg>
        </div>
        <div>
          <p class="text-sm text-gray-500 font-medium">Destino</p>
          <p class="text-gray-900 font-semibold">{{ destination }}</p>
        </div>
      </div>

      <!-- Warning de puntos en ruta -->
      <div v-if="criticalCount > 0" class="bg-orange-50 rounded-xl p-4 border border-orange-100 flex gap-3 items-center">
        <div class="text-orange-500 flex-shrink-0">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
        <p class="text-sm text-orange-800 font-medium leading-tight">
          La ruta sugerida incluye <strong class="font-bold">{{ criticalCount }} punto{{ criticalCount > 1 ? 's' : '' }} crítico{{ criticalCount > 1 ? 's' : '' }}</strong> para {{ userType }}.
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
withDefaults(
  defineProps<{
    destination?: string;
    criticalCount?: number;
    userType?: string;
  }>(),
  {
    destination: 'San Isidro, Centro Financiero',
    criticalCount: 2,
    userType: 'ciclistas',
  }
);
</script>
