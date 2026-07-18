import { ref, watch, onMounted } from 'vue';

const isDark = ref(false);

export function useDarkMode() {
  function toggleDarkMode() {
    isDark.value = !isDark.value;
    updateTheme();
  }

  function updateTheme() {
    if (isDark.value) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    }
  }

  onMounted(() => {
    // Evitar que onMounted se ejecute múltiples veces y resetee el estado
    // si ya se inicializó
    if (document.documentElement.classList.contains('dark')) {
      isDark.value = true;
      return;
    }

    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
      isDark.value = true;
    } else {
      isDark.value = false;
    }
    updateTheme();
  });

  return {
    isDark,
    toggleDarkMode,
  };
}
