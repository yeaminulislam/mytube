/**
 * MyTube - YouTube Data API v3 Service
 * আমিনের জন্য: এখানেই গুগলের সাথে কথা বলা হয়!
 * 
 * YouTube Data API v3 - Full Functional Integration
 * Docs: https://developers.google.com/youtube/v3/docs
 */

import { videos as mockVideos, shorts as mockShorts } from '../data/mockVideos';

// API Keys from .env
const API_KEY = import.meta.env.VITE_YOUTUBE_API_KEY;
const BASE_URL = 'https://www.googleapis.com/youtube/v3';
const USE_MOCK = !API_KEY || API_KEY === 'YOUR_YOUTUBE_API_KEY_HERE';

console.log(`🔑 YouTube API Mode: ${USE_MOCK ? 'MOCK (Demo)' : 'REAL (YouTube Data API v3)'}`);

// Helper: Format view count like YouTube
export const formatViewCount = (count) => {
  if (!count) return '0';
  const num = parseInt(count);
  if (num >= 10000000) return `${(num / 10000000).toFixed(1)}Cr`;
  if (num >= 1000000) return `${(num / 1000000).toFixed(1)}M`;
  if (num >= 1000) return `${(num / 1000).toFixed(1)}K`;
  return num.toString();
};

export const formatPublishedAt = (publishedAt) => {
  if (!publishedAt) return 'কিছুক্ষণ আগে';
  const now = new Date();
  const published = new Date(publishedAt);
  const diffMs = now - published;
  const diffSec = Math.floor(diffMs / 1000);
  const diffMin = Math.floor(diffSec / 60);
  const diffHour = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHour / 24);
  const diffMonth = Math.floor(diffDay / 30);
  const diffYear = Math.floor(diffDay / 365);

  if (diffYear > 0) return `${diffYear} বছর আগে`;
  if (diffMonth > 0) return `${diffMonth} মাস আগে`;
  if (diffDay > 0) return `${diffDay} দিন আগে`;
  if (diffHour > 0) return `${diffHour} ঘন্টা আগে`;
  if (diffMin > 0) return `${diffMin} মিনিট আগে`;
  return 'এখনই';
};

export const parseDuration = (isoDuration) => {
  if (!isoDuration) return '0:00';
  // ISO 8601: PT1H2M10S -> 1:02:10
  const match = isoDuration.match(/PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?/);
  if (!match) return '0:00';
  const hours = parseInt(match[1] || 0);
  const minutes = parseInt(match[2] || 0);
  const seconds = parseInt(match[3] || 0);
  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  }
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
};

// Core API caller with error handling
async function youtubeFetch(endpoint, params = {}) {
  if (USE_MOCK) throw new Error('MOCK_MODE');

  const url = new URL(`${BASE_URL}/${endpoint}`);
  url.searchParams.append('key', API_KEY);
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null) url.searchParams.append(k, v);
  });

  console.log(`📡 YouTube API Call: ${endpoint}`, params);

  const res = await fetch(url.toString());
  const data = await res.json();

  if (!res.ok) {
    console.error('YouTube API Error:', data);
    throw new Error(data.error?.message || 'YouTube API Error');
  }

  return data;
}

// 1. SEARCH VIDEOS - ইউটিউবের মতো সার্চ
export async function searchVideos(query, pageToken = '', maxResults = 20) {
  if (USE_MOCK) {
    // Mock search with filtering
    await new Promise(r => setTimeout(r, 300)); // Simulate network
    const filtered = mockVideos.filter(v => 
      v.title.toLowerCase().includes(query.toLowerCase()) ||
      v.channel.toLowerCase().includes(query.toLowerCase())
    );
    return {
      videos: filtered,
      nextPageToken: '',
      isMock: true
    };
  }

  try {
    // Step 1: Search
    const searchData = await youtubeFetch('search', {
      part: 'snippet',
      q: query,
      type: 'video',
      maxResults,
      pageToken,
      regionCode: 'BD',
      relevanceLanguage: 'bn'
    });

    const videoIds = searchData.items.map(item => item.id.videoId).join(',');

    // Step 2: Get video details (views, duration, etc.)
    const detailsData = await youtubeFetch('videos', {
      part: 'contentDetails,statistics,snippet',
      id: videoIds
    });

    const videos = detailsData.items.map(item => ({
      id: item.id,
      title: item.snippet.title,
      channel: item.snippet.channelTitle,
      channelId: item.snippet.channelId,
      channelAvatar: `https://i.pravatar.cc/100?u=${item.snippet.channelId}`,
      views: formatViewCount(item.statistics.viewCount),
      rawViews: item.statistics.viewCount,
      timestamp: formatPublishedAt(item.snippet.publishedAt),
      publishedAt: item.snippet.publishedAt,
      duration: parseDuration(item.contentDetails.duration),
      thumbnail: item.snippet.thumbnails.high?.url || item.snippet.thumbnails.medium?.url,
      category: 'সার্চ',
      verified: false,
      description: item.snippet.description,
      tags: item.snippet.tags || [],
      likeCount: formatViewCount(item.statistics.likeCount),
      commentCount: item.statistics.commentCount
    }));

    return {
      videos,
      nextPageToken: searchData.nextPageToken,
      isMock: false,
      raw: searchData
    };
  } catch (error) {
    console.error('Search failed, falling back to mock:', error);
    return {
      videos: mockVideos.filter(v => v.title.toLowerCase().includes(query.toLowerCase())),
      nextPageToken: '',
      isMock: true,
      error: error.message
    };
  }
}

// 2. POPULAR VIDEOS - ট্রেন্ডিং / হোম ফিড
export async function getPopularVideos(categoryId = '', pageToken = '', maxResults = 24) {
  if (USE_MOCK) {
    await new Promise(r => setTimeout(r, 400));
    return {
      videos: mockVideos,
      nextPageToken: '',
      isMock: true
    };
  }

  try {
    const data = await youtubeFetch('videos', {
      part: 'contentDetails,statistics,snippet',
      chart: 'mostPopular',
      regionCode: 'BD',
      maxResults,
      pageToken,
      videoCategoryId: categoryId || undefined
    });

    const videos = data.items.map(item => ({
      id: item.id,
      title: item.snippet.title,
      channel: item.snippet.channelTitle,
      channelId: item.snippet.channelId,
      channelAvatar: `https://i.pravatar.cc/100?u=${item.snippet.channelId}`,
      views: formatViewCount(item.statistics.viewCount),
      rawViews: item.statistics.viewCount,
      timestamp: formatPublishedAt(item.snippet.publishedAt),
      publishedAt: item.snippet.publishedAt,
      duration: parseDuration(item.contentDetails.duration),
      thumbnail: item.snippet.thumbnails.high?.url,
      category: item.snippet.categoryId,
      verified: true,
      description: item.snippet.description,
      likeCount: formatViewCount(item.statistics.likeCount)
    }));

    return {
      videos,
      nextPageToken: data.nextPageToken,
      isMock: false
    };
  } catch (error) {
    console.error('Popular failed, mock:', error);
    return { videos: mockVideos, nextPageToken: '', isMock: true, error: error.message };
  }
}

// 3. VIDEO DETAILS - একটা ভিডিওর সম্পূর্ণ তথ্য
export async function getVideoDetails(videoId) {
  if (USE_MOCK) {
    const mock = mockVideos.find(v => v.id === videoId) || mockVideos[0];
    return { ...mock, isMock: true };
  }

  try {
    const data = await youtubeFetch('videos', {
      part: 'contentDetails,statistics,snippet',
      id: videoId
    });

    if (!data.items.length) throw new Error('Video not found');

    const item = data.items[0];
    return {
      id: item.id,
      title: item.snippet.title,
      channel: item.snippet.channelTitle,
      channelId: item.snippet.channelId,
      channelAvatar: `https://i.pravatar.cc/100?u=${item.snippet.channelId}`,
      views: formatViewCount(item.statistics.viewCount),
      rawViews: item.statistics.viewCount,
      timestamp: formatPublishedAt(item.snippet.publishedAt),
      publishedAt: item.snippet.publishedAt,
      duration: parseDuration(item.contentDetails.duration),
      thumbnail: item.snippet.thumbnails.high?.url,
      category: item.snippet.categoryId,
      verified: true,
      description: item.snippet.description,
      tags: item.snippet.tags || [],
      likeCount: formatViewCount(item.statistics.likeCount),
      rawLikeCount: item.statistics.likeCount,
      commentCount: item.statistics.commentCount,
      isMock: false,
      raw: item
    };
  } catch (error) {
    const mock = mockVideos.find(v => v.id === videoId) || mockVideos[0];
    return { ...mock, isMock: true, error: error.message };
  }
}

// 4. RELATED VIDEOS
export async function getRelatedVideos(videoId, maxResults = 12) {
  if (USE_MOCK) {
    return mockVideos.filter(v => v.id !== videoId).slice(0, maxResults);
  }

  try {
    // YouTube removed relatedToVideoId, so we search with same title keywords
    const videoDetails = await getVideoDetails(videoId);
    const keywords = videoDetails.title?.split(' ').slice(0, 3).join(' ') || 'android';

    const searchData = await youtubeFetch('search', {
      part: 'snippet',
      q: keywords,
      type: 'video',
      maxResults,
      regionCode: 'BD'
    });

    const videoIds = searchData.items.map(i => i.id.videoId).join(',');
    const details = await youtubeFetch('videos', {
      part: 'contentDetails,statistics,snippet',
      id: videoIds
    });

    return details.items.map(item => ({
      id: item.id,
      title: item.snippet.title,
      channel: item.snippet.channelTitle,
      channelId: item.snippet.channelId,
      thumbnail: item.snippet.thumbnails.medium?.url,
      views: formatViewCount(item.statistics.viewCount),
      timestamp: formatPublishedAt(item.snippet.publishedAt),
      duration: parseDuration(item.contentDetails.duration)
    }));
  } catch (error) {
    return mockVideos.filter(v => v.id !== videoId).slice(0, maxResults);
  }
}

// 5. COMMENTS
export async function getVideoComments(videoId, maxResults = 20) {
  if (USE_MOCK) {
    return [
      { id: '1', author: 'রহিম', avatar: 'https://i.pravatar.cc/100?img=10', text: 'অসাধারণ টিউটোরিয়াল! অনেক কিছু শিখলাম।', time: '2 ঘন্টা আগে', likes: 24 },
      { id: '2', author: 'করিম', avatar: 'https://i.pravatar.cc/100?img=11', text: 'আমিন ভাই, পরের ভিডিওতে Firebase নিয়ে বিস্তারিত দেখাবেন প্লিজ।', time: '5 ঘন্টা আগে', likes: 12 },
    ];
  }

  try {
    const data = await youtubeFetch('commentThreads', {
      part: 'snippet',
      videoId,
      maxResults,
      order: 'relevance'
    });

    return data.items.map(item => ({
      id: item.id,
      author: item.snippet.topLevelComment.snippet.authorDisplayName,
      avatar: item.snippet.topLevelComment.snippet.authorProfileImageUrl,
      text: item.snippet.topLevelComment.snippet.textDisplay,
      time: formatPublishedAt(item.snippet.topLevelComment.snippet.publishedAt),
      likes: item.snippet.topLevelComment.snippet.likeCount,
      replyCount: item.snippet.totalReplyCount
    }));
  } catch (error) {
    console.warn('Comments disabled or API error:', error.message);
    return [];
  }
}

// 6. SEARCH SUGGESTIONS - YouTube Autocomplete
export async function getSearchSuggestions(query) {
  if (!query) return [];
  
  try {
    // YouTube Suggest API (no key needed, but CORS may block - use proxy or fallback)
    const res = await fetch(`https://suggestqueries.google.com/complete/search?client=youtube&ds=yt&q=${encodeURIComponent(query)}&hl=bn`, {
      // mode: 'no-cors' won't work for JSON, so we try direct
    });
    const text = await res.text();
    // Response is JSONP: window.google.ac.h(["query",[["suggestion1"],["suggestion2"]]])
    const json = JSON.parse(text.substring(text.indexOf('(') + 1, text.lastIndexOf(')')));
    return json[1]?.map(item => item[0]) || [];
  } catch (error) {
    // Fallback suggestions
    const mockSuggestions = [
      `${query} bangla tutorial`,
      `${query} android development`,
      `${query} full course`,
      `${query} 2025`,
      `${query} for beginners`
    ];
    return mockSuggestions.slice(0, 5);
  }
}

// 7. CHANNEL DETAILS
export async function getChannelDetails(channelId) {
  if (USE_MOCK) {
    return {
      id: channelId,
      title: 'Learn With Amin',
      avatar: 'https://i.pravatar.cc/100?img=1',
      banner: 'https://picsum.photos/1200/300',
      subscribers: '1.2M',
      videoCount: '245',
      description: 'Android Development Tutorials in Bangla'
    };
  }

  try {
    const data = await youtubeFetch('channels', {
      part: 'snippet,statistics',
      id: channelId
    });

    const channel = data.items[0];
    return {
      id: channel.id,
      title: channel.snippet.title,
      avatar: channel.snippet.thumbnails.high?.url,
      banner: channel.brandingSettings?.image?.bannerExternalUrl,
      subscribers: formatViewCount(channel.statistics.subscriberCount),
      videoCount: channel.statistics.videoCount,
      viewCount: formatViewCount(channel.statistics.viewCount),
      description: channel.snippet.description
    };
  } catch (error) {
    return null;
  }
}

// 8. SHORTS - YouTube Shorts are vertical videos < 60s
export async function getShorts(query = 'shorts', maxResults = 12) {
  if (USE_MOCK) {
    await new Promise(r => setTimeout(r, 300));
    return mockShorts;
  }

  try {
    const searchData = await youtubeFetch('search', {
      part: 'snippet',
      q: `${query} #shorts`,
      type: 'video',
      videoDuration: 'short', // < 4 minutes, filter for shorts
      maxResults,
      regionCode: 'BD'
    });

    const videoIds = searchData.items.map(i => i.id.videoId).join(',');
    const details = await youtubeFetch('videos', {
      part: 'contentDetails,statistics,snippet',
      id: videoIds
    });

    // Filter videos that are actually short (< 60s) and vertical
    return details.items
      .filter(item => {
        const duration = parseDuration(item.contentDetails.duration);
        const [min, sec] = duration.split(':').map(Number);
        const totalSec = (min || 0) * 60 + (sec || 0);
        return totalSec <= 60;
      })
      .map(item => ({
        id: item.id,
        videoId: item.id,
        title: item.snippet.title,
        channel: item.snippet.channelTitle,
        views: formatViewCount(item.statistics.viewCount),
        likes: formatViewCount(item.statistics.likeCount),
        thumbnail: item.snippet.thumbnails.high?.url
      }));
  } catch (error) {
    return mockShorts;
  }
}

// Export API status
export const apiStatus = {
  isMock: USE_MOCK,
  hasApiKey: !!API_KEY && API_KEY !== 'YOUR_YOUTUBE_API_KEY_HERE',
  apiKey: API_KEY ? `${API_KEY.substring(0, 8)}...` : 'Not set'
};
