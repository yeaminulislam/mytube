/**
 * MyVanced - Return YouTube Dislike
 * Vanced এর আরেকটা ফিচার - Dislike count ফিরিয়ে আনা!
 * YouTube 2021 এ Dislike count hide করে দিয়েছিল, Vanced এটা ফিরিয়ে আনে
 * 
 * API: https://returnyoutubedislikeapi.com/
 */

// Return YouTube Dislike API - Free!
const RYD_API = 'https://returnyoutubedislikeapi.com';

export async function getDislikeData(videoId) {
  try {
    console.log(`👎 RYD: Fetching dislikes for ${videoId}`);

    const url = `${RYD_API}/votes?videoId=${videoId}`;
    const res = await fetch(url);
    
    if (!res.ok) throw new Error('RYD API error');
    
    const data = await res.json();
    
    console.log('✅ RYD Data:', data);
    
    return {
      likes: data.likes,
      dislikes: data.dislikes,
      rating: data.rating, // 1-5
      viewCount: data.viewCount,
      deleted: data.deleted
    };

  } catch (error) {
    console.warn('RYD error:', error.message);
    return null;
  }
}

export function formatDislikeCount(dislikes) {
  if (!dislikes) return '0';
  if (dislikes >= 1000000) return `${(dislikes / 1000000).toFixed(1)}M`;
  if (dislikes >= 1000) return `${(dislikes / 1000).toFixed(1)}K`;
  return dislikes.toString();
}

// Like ratio bar (like YouTube)
export function getLikeRatio(likes, dislikes) {
  const total = likes + dislikes;
  if (total === 0) return 50;
  return (likes / total) * 100;
}
