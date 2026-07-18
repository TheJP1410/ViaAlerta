# ViaAlerta
VíaAlerta: Plataforma web que utiliza datos abiertos del gobierno para prevenir accidentes de tránsito. Incluye un mapa interactivo que alerta a peatones y ciclistas sobre cruces peligrosos, y un panel predictivo para que las municipalidades mejoren la seguridad vial.

## Monorepo structure
- `frontend/`: Astro + TypeScript + Tailwind CSS responsive web app scaffold.
- `backend/`: Express REST API scaffold with CORS and mock hotspot data.

## Run frontend
```bash
cd frontend
npm install
npm run dev
```

## Run backend
```bash
cd backend
npm install
npm run start
```

### Backend endpoints
- `GET /api/health`
- `GET /api/hotspots`
