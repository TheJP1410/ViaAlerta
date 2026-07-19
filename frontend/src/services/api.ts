import mockAccidents from '../data/mockAccidents.json';

// ============================================================
// Tipos
// ============================================================

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

export interface DashboardStats {
  totalIncidents: number;
  criticalPoints: number;
  totalZones: number;
  topDistrict: string;
}

// ============================================================
// Configuración
// ============================================================


const USE_MOCK = false;
const API_BASE_URL = 'https://viaalerta.onrender.com/api';

// Timeout para las peticiones fetch (ms)
const FETCH_TIMEOUT = 5000;

// ============================================================
// Helpers
// ============================================================

/**
 * Fetch con timeout integrado para evitar que la app se quede
 * colgada si el backend no responde.
 */
async function fetchWithTimeout(url: string, timeoutMs = FETCH_TIMEOUT): Promise<Response> {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const response = await fetch(url, { signal: controller.signal });
    clearTimeout(timer);
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    return response;
  } catch (error) {
    clearTimeout(timer);
    throw error;
  }
}

// ============================================================
// Endpoints
// ============================================================

/**
 * Obtiene todos los puntos críticos de accidentes.
 * En modo mock devuelve los datos locales.
 * En modo real llama a GET /api/puntos-criticos
 */
export async function getAccidentPoints(): Promise<AccidentPoint[]> {
  if (USE_MOCK) {
    return mockAccidents as AccidentPoint[];
  }

  try {
    const response = await fetchWithTimeout(`${API_BASE_URL}/puntos-criticos`);
    return await response.json();
  } catch (error) {
    console.error('[VíaAlerta API] Error obteniendo puntos críticos:', error);
    console.warn('[VíaAlerta API] Usando datos de respaldo (mock).');
    return mockAccidents as AccidentPoint[];
  }
}

/**
 * Obtiene un punto crítico por su ID.
 */
export async function getAccidentById(id: string): Promise<AccidentPoint | null> {
  if (USE_MOCK) {
    return (mockAccidents as AccidentPoint[]).find(p => p.id === id) ?? null;
  }

  try {
    const response = await fetchWithTimeout(`${API_BASE_URL}/puntos-criticos/${id}`);
    return await response.json();
  } catch (error) {
    console.error(`[VíaAlerta API] Error obteniendo punto ${id}:`, error);
    return (mockAccidents as AccidentPoint[]).find(p => p.id === id) ?? null;
  }
}

/**
 * Obtiene estadísticas generales del dashboard.
 * En modo mock las calcula localmente a partir de los datos.
 */
export async function getDashboardStats(): Promise<DashboardStats> {
  if (USE_MOCK) {
    const data = mockAccidents as AccidentPoint[];
    const districtCounts: Record<string, number> = {};
    data.forEach(p => {
      districtCounts[p.district] = (districtCounts[p.district] || 0) + p.incidents;
    });
    const topDistrict = Object.entries(districtCounts).sort((a, b) => b[1] - a[1])[0]?.[0] ?? '—';

    return {
      totalIncidents: data.reduce((acc, p) => acc + p.incidents, 0),
      criticalPoints: data.filter(p => p.riskLevel === 'Crítico' || p.riskLevel === 'Alto').length,
      totalZones: data.length,
      topDistrict,
    };
  }

  try {
    const response = await fetchWithTimeout(`${API_BASE_URL}/stats`);
    return await response.json();
  } catch (error) {
    console.error('[VíaAlerta API] Error obteniendo estadísticas:', error);
    // Fallback: calcular localmente
    const data = mockAccidents as AccidentPoint[];
    return {
      totalIncidents: data.reduce((acc, p) => acc + p.incidents, 0),
      criticalPoints: data.filter(p => p.riskLevel === 'Crítico' || p.riskLevel === 'Alto').length,
      totalZones: data.length,
      topDistrict: '—',
    };
  }
}

/**
 * Obtiene puntos filtrados por distrito.
 * Preparado para cuando el backend soporte query params.
 */
export async function getPointsByDistrict(district: string): Promise<AccidentPoint[]> {
  if (USE_MOCK) {
    return (mockAccidents as AccidentPoint[]).filter(
      p => p.district.toLowerCase().includes(district.toLowerCase())
    );
  }

  try {
    const response = await fetchWithTimeout(
      `${API_BASE_URL}/puntos-criticos?district=${encodeURIComponent(district)}`
    );
    return await response.json();
  } catch (error) {
    console.error(`[VíaAlerta API] Error filtrando por distrito "${district}":`, error);
    return (mockAccidents as AccidentPoint[]).filter(
      p => p.district.toLowerCase().includes(district.toLowerCase())
    );
  }
}
