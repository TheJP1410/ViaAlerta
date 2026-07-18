import mockData from '../data/mockAccidents.json';

export interface AccidentPoint {
  id: string;
  lat: number;
  lng: number;
  intersection: string;
  district: string;
  riskLevel: string;
  incidents: number;
  lastIncident: string;
  mainVictims: string[];
  commonCause: string;
}

const API_URL = 'http://localhost:8080/api/puntos-criticos'; // Backend de Percy

/**
 * Obtiene los puntos críticos.
 * NOTA: Actualmente usa datos mockeados. Cuando el backend esté listo,
 * cambia 'USE_MOCK' a false.
 */
export async function getAccidentPoints(): Promise<AccidentPoint[]> {
  const USE_MOCK = true;

  if (USE_MOCK) {
    // Simula tiempo de red
    return new Promise((resolve) => setTimeout(() => resolve(mockData), 300));
  } else {
    try {
      const response = await fetch(API_URL);
      if (!response.ok) throw new Error('Error al obtener datos');
      return await response.json();
    } catch (error) {
      console.error("Fetch error:", error);
      // Fallback a mock data en caso de error (ej. CORS)
      return mockData;
    }
  }
}
