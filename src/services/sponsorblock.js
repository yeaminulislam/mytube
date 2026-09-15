/**
 * MyVanced - SponsorBlock Integration
 * Vanced এর সবচেয়ে মজার ফিচার - Sponsor Auto Skip!
 * 
 * SponsorBlock: Community driven database - ইউজাররা মার্ক করে কোথায় Sponsor আছে
 * API: https://sponsor.ajay.app/
 * Docs: https://wiki.sponsor.ajay.app/
 */

// SponsorBlock API - Free, No Key Needed!
const SPONSORBLOCK_API = 'https://sponsor.ajay.app';

/**
 * Get Sponsor Segments for a video
 * Vanced যেভাবে করে - ভিডিওর Sponsor part auto skip
 */
export async function getSponsorSegments(videoId) {
  try {
    console.log(`🎬 SponsorBlock: Checking ${videoId}`);

    const categories = [
      'sponsor',      // Sponsor
      'intro',        // Intro
      'outro',        // Outro
      'interaction',  // Subscribe reminder
      'selfpromo',    // Self promotion
      'music_offtopic' // Non-music in music video
    ];

    const url = `${SPONSORBLOCK_API}/api/skipSegments?videoID=${videoId}&categories=${JSON.stringify(categories)}`;
    
    const res = await fetch(url);
    
    if (!res.ok) {
      if (res.status === 404) {
        console.log('No sponsors found for', videoId);
        return [];
      }
      throw new Error('SponsorBlock API error');
    }

    const segments = await res.json();
    
    console.log(`✅ Found ${segments.length} sponsor segments:`, segments);
    
    // Format: [{segment: [start, end], category: "sponsor"}, ...]
    return segments.map(seg => ({
      start: seg.segment[0],
      end: seg.segment[1],
      category: seg.category,
      uuid: seg.UUID
    }));

  } catch (error) {
    console.warn('SponsorBlock error:', error.message);
    return [];
  }
}

/**
 * Skip sponsors automatically - Vanced Logic
 */
export function setupSponsorBlockSkip(videoElement, segments) {
  if (!videoElement || !segments.length) return;

  console.log('🎯 Setting up auto-skip for', segments.length, 'segments');

  const skipHandler = () => {
    const currentTime = videoElement.currentTime;
    
    for (const seg of segments) {
      // If current time is inside sponsor segment, skip to end
      if (currentTime >= seg.start && currentTime < seg.end - 0.5) {
        console.log(`⏭️ Skipping ${seg.category}: ${seg.start} -> ${seg.end}`);
        videoElement.currentTime = seg.end;
        
        // Show notification (like Vanced)
        showSkipNotification(seg);
        break;
      }
    }
  };

  videoElement.addEventListener('timeupdate', skipHandler);

  // Return cleanup function
  return () => {
    videoElement.removeEventListener('timeupdate', skipHandler);
  };
}

function showSkipNotification(segment) {
  // Create Vanced-style skip notification
  const existing = document.getElementById('sponsorblock-notice');
  if (existing) existing.remove();

  const notice = document.createElement('div');
  notice.id = 'sponsorblock-notice';
  notice.style.cssText = `
    position: fixed;
    bottom: 100px;
    left: 50%;
    transform: translateX(-50%);
    background: #212121;
    color: white;
    padding: 12px 20px;
    border-radius: 24px;
    font-size: 14px;
    z-index: 9999;
    border: 1px solid #303030;
    box-shadow: 0 4px 12px rgba(0,0,0,0.5);
  `;
  notice.innerHTML = `⏭️ ${getCategoryEmoji(segment.category)} ${getCategoryName(segment.category)} স্কিপ করা হলো`;

  document.body.appendChild(notice);

  setTimeout(() => notice.remove(), 3000);
}

function getCategoryEmoji(cat) {
  const emojis = {
    sponsor: '💰',
    intro: '🎬',
    outro: '👋',
    interaction: '🔔',
    selfpromo: '📢',
    music_offtopic: '🎵'
  };
  return emojis[cat] || '⏭️';
}

function getCategoryName(cat) {
  const names = {
    sponsor: 'Sponsor',
    intro: 'Intro',
    outro: 'Outro',
    interaction: 'Interaction',
    selfpromo: 'Self Promo',
    music_offtopic: 'Non-Music'
  };
  return names[cat] || cat;
}

/**
 * Submit Sponsor Segment (Community contribution)
 * Vanced এ ইউজাররা Sponsor মার্ক করতে পারে
 */
export async function submitSponsorSegment(videoId, startTime, endTime, category = 'sponsor') {
  // This requires userID - for demo, we just log
  console.log(`📤 Would submit: ${videoId} [${startTime}-${endTime}] as ${category}`);
  
  // Real implementation needs userID from https://sponsor.ajay.app/api/userID
  // POST /api/skipSegments with userID, videoID, segments
  
  return { success: true, message: 'Demo mode - would submit in real app' };
}
