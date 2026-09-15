/**
 * MyTube Service Worker - PWA Offline Support
 * আমিনের জন্য: Service Worker কিভাবে কাজ করে
 */

const CACHE_NAME = 'mytube-v2';
const urlsToCache = [
  '/',
  '/index.html',
  '/manifest.json'
];

// Install - Cache files
self.addEventListener('install', (event) => {
  console.log('📦 MyTube SW: Installing');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => {
        return cache.addAll(urlsToCache);
      })
  );
  self.skipWaiting();
});

// Activate - Clean old caches
self.addEventListener('activate', (event) => {
  console.log('✅ MyTube SW: Activated');
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (cacheName !== CACHE_NAME) {
            console.log('🗑️ Deleting old cache:', cacheName);
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
  self.clients.claim();
});

// Fetch - Network first, then cache (for YouTube API we always use network)
self.addEventListener('fetch', (event) => {
  const url = new URL(event.request.url);

  // Don't cache YouTube API calls - always fresh
  if (url.hostname.includes('googleapis.com') || url.hostname.includes('youtube.com') || url.hostname.includes('google.com')) {
    event.respondWith(fetch(event.request));
    return;
  }

  // For other requests: Network first, cache fallback
  event.respondWith(
    fetch(event.request)
      .then((response) => {
        // Cache successful responses
        if (response.ok) {
          const responseClone = response.clone();
          caches.open(CACHE_NAME).then((cache) => {
            cache.put(event.request, responseClone);
          });
        }
        return response;
      })
      .catch(() => {
        // Offline - return from cache
        return caches.match(event.request);
      })
  );
});

// Push notification (future)
self.addEventListener('push', (event) => {
  const data = event.data ? event.data.json() : { title: 'MyTube', body: 'নতুন ভিডিও এসেছে!' };
  event.waitUntil(
    self.registration.showNotification(data.title, {
      body: data.body,
      icon: 'https://www.youtube.com/s/desktop/12d6b690/img/favicon_32x32.png',
      badge: 'https://www.youtube.com/s/desktop/12d6b690/img/favicon_32x32.png'
    })
  );
});
