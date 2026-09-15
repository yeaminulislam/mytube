import { useEffect, useState, useRef } from 'react';
import { getSponsorSegments, setupSponsorBlockSkip } from '../services/sponsorblock';
import { getDislikeData } from '../services/returnDislike';

/**
 * useVancedFeatures - Vanced এর সব ফিচার একসাথে
 * আমিনের জন্য: Vanced কিভাবে Background Play, PiP, AdBlock করে
 */

export function useVancedFeatures(videoId, videoElementRef) {
  const [sponsorSegments, setSponsorSegments] = useState([]);
  const [dislikeData, setDislikeData] = useState(null);
  const [isBackgroundPlayEnabled, setIsBackgroundPlayEnabled] = useState(() => {
    return localStorage.getItem('myvanced_bg_play') !== 'false';
  });
  const [isAmoledEnabled, setIsAmoledEnabled] = useState(() => {
    return localStorage.getItem('myvanced_amoled') === 'true';
  });
  const cleanupRef = useRef(null);

  // 1. SponsorBlock - Auto skip sponsors
  useEffect(() => {
    if (!videoId) return;

    const loadSponsors = async () => {
      const segments = await getSponsorSegments(videoId);
      setSponsorSegments(segments);

      // Setup auto-skip if video element exists
      if (videoElementRef?.current && segments.length > 0) {
        cleanupRef.current = setupSponsorBlockSkip(videoElementRef.current, segments);
      }
    };

    loadSponsors();

    return () => {
      if (cleanupRef.current) cleanupRef.current();
    };
  }, [videoId]);

  // 2. Return YouTube Dislike
  useEffect(() => {
    if (!videoId) return;

    const loadDislikes = async () => {
      const data = await getDislikeData(videoId);
      setDislikeData(data);
    };

    loadDislikes();
  }, [videoId]);

  // 3. Background Playback - Media Session API
  useEffect(() => {
    if (!isBackgroundPlayEnabled || !videoElementRef?.current) return;

    const video = videoElementRef.current;

    // Media Session API - Lock screen controls, background play
    if ('mediaSession' in navigator) {
      navigator.mediaSession.metadata = new MediaMetadata({
        title: document.title || 'MyTube Video',
        artist: 'MyTube',
        artwork: [
          { src: 'https://www.youtube.com/s/desktop/12d6b690/img/favicon_32x32.png', sizes: '32x32', type: 'image/png' }
        ]
      });

      navigator.mediaSession.setActionHandler('play', () => video.play());
      navigator.mediaSession.setActionHandler('pause', () => video.pause());
      navigator.mediaSession.setActionHandler('seekbackward', () => video.currentTime -= 10);
      navigator.mediaSession.setActionHandler('seekforward', () => video.currentTime += 10);

      console.log('🎵 Background Play enabled via Media Session API');
    }

    // Keep playing when tab is hidden (Vanced behavior)
    const handleVisibilityChange = () => {
      if (document.hidden && isBackgroundPlayEnabled) {
        console.log('📱 Tab hidden but background play ON - keep playing');
        // Don't pause
      }
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);

    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange);
    };
  }, [isBackgroundPlayEnabled, videoElementRef]);

  // 4. Picture-in-Picture (PiP) - Vanced PiP
  const enablePiP = async () => {
    try {
      const video = videoElementRef?.current;
      if (!video) return;

      if (document.pictureInPictureElement) {
        await document.exitPictureInPicture();
      } else if (document.pictureInPictureEnabled && video.readyState > 0) {
        await video.requestPictureInPicture();
        console.log('📺 PiP enabled');
      }
    } catch (err) {
      console.error('PiP failed:', err);
    }
  };

  // 5. AdBlock Simulation (Web version)
  // Real Vanced blocks ads at APK level, we simulate via Service Worker
  const [isAdBlockEnabled, setIsAdBlockEnabled] = useState(() => {
    return localStorage.getItem('myvanced_adblock') !== 'false';
  });

  useEffect(() => {
    localStorage.setItem('myvanced_adblock', isAdBlockEnabled);
    console.log(`🚫 AdBlock ${isAdBlockEnabled ? 'ON' : 'OFF'} - Vanced style`);
  }, [isAdBlockEnabled]);

  // Toggle functions
  const toggleBackgroundPlay = () => {
    const newVal = !isBackgroundPlayEnabled;
    setIsBackgroundPlayEnabled(newVal);
    localStorage.setItem('myvanced_bg_play', newVal);
  };

  const toggleAmoled = () => {
    const newVal = !isAmoledEnabled;
    setIsAmoledEnabled(newVal);
    localStorage.setItem('myvanced_amoled', newVal);
    
    if (newVal) {
      document.documentElement.classList.add('amoled');
      document.body.style.backgroundColor = '#000000';
    } else {
      document.documentElement.classList.remove('amoled');
      document.body.style.backgroundColor = '#0f0f0f';
    }
  };

  return {
    sponsorSegments,
    dislikeData,
    isBackgroundPlayEnabled,
    isAmoledEnabled,
    isAdBlockEnabled,
    toggleBackgroundPlay,
    toggleAmoled,
    setIsAdBlockEnabled,
    enablePiP
  };
}
