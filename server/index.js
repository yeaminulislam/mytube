/**
 * MyTube - Backend Proxy Server (Optional but Recommended)
 * আমিনের জন্য: Real World এ API Key frontend এ রাখা নিরাপদ নয়, তাই Backend Proxy ব্যবহার করা হয়
 * 
 * কেন Backend দরকার?
 * 1. API Key hide করা
 * 2. CORS handle করা
 * 3. Rate limiting
 * 4. User data save করা (History, Likes)
 * 
 * Run: node server/index.js
 */

import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3001;
const YOUTUBE_API_KEY = process.env.VITE_YOUTUBE_API_KEY || process.env.YOUTUBE_API_KEY;
const YOUTUBE_BASE = 'https://www.googleapis.com/youtube/v3';

app.use(cors());
app.use(express.json());

// Middleware: Log all requests
app.use((req, res, next) => {
  console.log(`📡 ${req.method} ${req.path}`, req.query);
  next();
});

// Health check
app.get('/', (req, res) => {
  res.json({
    status: 'MyTube API Server Running',
    mode: YOUTUBE_API_KEY ? 'REAL' : 'MOCK',
    endpoints: [
      'GET /api/search?q=android',
      'GET /api/videos?chart=mostPopular',
      'GET /api/video/:id',
      'GET /api/related/:id',
      'GET /api/comments/:id',
      'GET /api/channel/:id'
    ]
  });
});

// Proxy: Search videos
app.get('/api/search', async (req, res) => {
  const { q, pageToken = '', maxResults = 20 } = req.query;
  
  if (!q) return res.status(400).json({ error: 'Query required' });

  if (!YOUTUBE_API_KEY) {
    return res.json({ mock: true, message: 'Set YOUTUBE_API_KEY in .env for real data' });
  }

  try {
    // Step 1: Search
    const searchUrl = new URL(`${YOUTUBE_BASE}/search`);
    searchUrl.searchParams.append('key', YOUTUBE_API_KEY);
    searchUrl.searchParams.append('part', 'snippet');
    searchUrl.searchParams.append('q', q);
    searchUrl.searchParams.append('type', 'video');
    searchUrl.searchParams.append('maxResults', maxResults);
    searchUrl.searchParams.append('regionCode', 'BD');
    if (pageToken) searchUrl.searchParams.append('pageToken', pageToken);

    console.log(`🔍 Searching YouTube: ${q}`);

    const searchRes = await fetch(searchUrl);
    const searchData = await searchRes.json();

    if (!searchRes.ok) {
      console.error('YouTube Search Error:', searchData);
      return res.status(searchRes.status).json(searchData);
    }

    // Step 2: Get details
    const videoIds = searchData.items.map(i => i.id.videoId).join(',');
    const detailsUrl = new URL(`${YOUTUBE_BASE}/videos`);
    detailsUrl.searchParams.append('key', YOUTUBE_API_KEY);
    detailsUrl.searchParams.append('part', 'contentDetails,statistics,snippet');
    detailsUrl.searchParams.append('id', videoIds);

    const detailsRes = await fetch(detailsUrl);
    const detailsData = await detailsRes.json();

    res.json({
      videos: detailsData.items,
      nextPageToken: searchData.nextPageToken,
      searchRaw: searchData,
      mode: 'REAL'
    });

  } catch (error) {
    console.error('Proxy error:', error);
    res.status(500).json({ error: error.message });
  }
});

// Proxy: Popular videos
app.get('/api/videos', async (req, res) => {
  const { chart = 'mostPopular', pageToken = '', maxResults = 24 } = req.query;

  if (!YOUTUBE_API_KEY) {
    return res.json({ mock: true, message: 'Set API Key' });
  }

  try {
    const url = new URL(`${YOUTUBE_BASE}/videos`);
    url.searchParams.append('key', YOUTUBE_API_KEY);
    url.searchParams.append('part', 'contentDetails,statistics,snippet');
    url.searchParams.append('chart', chart);
    url.searchParams.append('regionCode', 'BD');
    url.searchParams.append('maxResults', maxResults);
    if (pageToken) url.searchParams.append('pageToken', pageToken);

    const response = await fetch(url);
    const data = await response.json();

    if (!response.ok) return res.status(response.status).json(data);

    res.json({ ...data, mode: 'REAL' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Proxy: Video details
app.get('/api/video/:id', async (req, res) => {
  const { id } = req.params;

  if (!YOUTUBE_API_KEY) {
    return res.json({ mock: true, id });
  }

  try {
    const url = new URL(`${YOUTUBE_BASE}/videos`);
    url.searchParams.append('key', YOUTUBE_API_KEY);
    url.searchParams.append('part', 'contentDetails,statistics,snippet');
    url.searchParams.append('id', id);

    const response = await fetch(url);
    const data = await response.json();

    res.json(data);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Proxy: Comments
app.get('/api/comments/:id', async (req, res) => {
  const { id } = req.params;
  const { maxResults = 20 } = req.query;

  if (!YOUTUBE_API_KEY) {
    return res.json({ mock: true, id });
  }

  try {
    const url = new URL(`${YOUTUBE_BASE}/commentThreads`);
    url.searchParams.append('key', YOUTUBE_API_KEY);
    url.searchParams.append('part', 'snippet');
    url.searchParams.append('videoId', id);
    url.searchParams.append('maxResults', maxResults);
    url.searchParams.append('order', 'relevance');

    const response = await fetch(url);
    const data = await response.json();

    res.json(data);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Start server
app.listen(PORT, '0.0.0.0', () => {
  console.log(`
🚀 MyTube Backend Server Running!

📍 Local: http://localhost:${PORT}
🌐 Network: http://0.0.0.0:${PORT}

🔑 YouTube API: ${YOUTUBE_API_KEY ? '✅ Connected' : '❌ Mock Mode - Set YOUTUBE_API_KEY in .env'}

📚 Endpoints:
   GET /api/search?q=android
   GET /api/videos
   GET /api/video/:id
   GET /api/comments/:id

💡 Frontend .env এ VITE_API_BASE_URL=http://localhost:${PORT} দিন
   তাহলে API Key frontend এ expose হবে না!
`);
});
