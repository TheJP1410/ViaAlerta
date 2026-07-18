const express = require('express');
const cors = require('cors');
const hotspots = require('./data.json');

const app = express();
const port = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());

app.get('/api/health', (_req, res) => {
  res.json({ status: 'ok' });
});

app.get('/api/hotspots', (_req, res) => {
  res.json(hotspots);
});

app.listen(port, () => {
  console.log(`Backend running on port ${port}`);
});
