/**
 * MyTube - Google OAuth 2.0 Service
 * আমিনের জন্য: গুগলের সাথে Authentication কিভাবে হয়
 * 
 * Docs:
 * - Google Identity Services: https://developers.google.com/identity/gsi/web
 * - OAuth 2.0: https://developers.google.com/identity/protocols/oauth2
 */

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const USE_MOCK = !GOOGLE_CLIENT_ID || GOOGLE_CLIENT_ID === 'YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com';

console.log(`🔐 Google Auth Mode: ${USE_MOCK ? 'MOCK' : 'REAL (Google Identity Services)'}`);

// Load Google Identity Services script
function loadGoogleScript() {
  return new Promise((resolve, reject) => {
    if (window.google?.accounts) {
      resolve(window.google);
      return;
    }

    const existing = document.getElementById('google-identity-script');
    if (existing) {
      existing.onload = () => resolve(window.google);
      return;
    }

    const script = document.createElement('script');
    script.id = 'google-identity-script';
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = () => {
      console.log('✅ Google Identity Services loaded');
      resolve(window.google);
    };
    script.onerror = reject;
    document.head.appendChild(script);
  });
}

// Decode JWT token (Google ID token)
export function decodeJwt(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error('JWT decode failed:', e);
    return null;
  }
}

// Initialize Google Auth - গুগল ক্লায়েন্ট কিভাবে কথা বলে
export async function initGoogleAuth() {
  if (USE_MOCK) {
    console.log('⚠️ Using Mock Auth - Set VITE_GOOGLE_CLIENT_ID for real Google Login');
    return { isMock: true };
  }

  try {
    await loadGoogleScript();

    // Initialize Google Identity Services
    window.google.accounts.id.initialize({
      client_id: GOOGLE_CLIENT_ID,
      callback: handleCredentialResponse,
      auto_select: false,
      cancel_on_tap_outside: true,
      // For One Tap
      context: 'signin',
      ux_mode: 'popup',
      // For Access Token (YouTube API scopes)
      // scope is for OAuth 2.0 token, not ID token
    });

    console.log('✅ Google Auth initialized with Client ID:', GOOGLE_CLIENT_ID.substring(0, 20) + '...');
    return { isMock: false, google: window.google };
  } catch (error) {
    console.error('Google Auth init failed:', error);
    return { isMock: true, error: error.message };
  }
}

// Handle Google credential response (ID Token)
let authCallback = null;

function handleCredentialResponse(response) {
  console.log('🔑 Google Credential Response:', response);
  
  const payload = decodeJwt(response.credential);
  console.log('👤 Decoded user:', payload);

  if (payload && authCallback) {
    const user = {
      id: payload.sub,
      email: payload.email,
      name: payload.name,
      avatar: payload.picture,
      givenName: payload.given_name,
      familyName: payload.family_name,
      emailVerified: payload.email_verified,
      provider: 'google',
      idToken: response.credential,
      joinedAt: new Date().toISOString(),
      // For YouTube API
      accessToken: null // Will be obtained separately if needed
    };

    // Save to localStorage
    localStorage.setItem('mytube_user', JSON.stringify(user));
    localStorage.setItem('mytube_id_token', response.credential);

    authCallback(user);
  }
}

// Sign in with Google - Popup
export async function signInWithGoogle() {
  if (USE_MOCK) {
    // Mock Google User
    const mockUser = {
      id: 'mock_' + Date.now(),
      email: 'amin@gmail.com',
      name: 'Amin Islam',
      avatar: 'https://i.pravatar.cc/100?img=8',
      givenName: 'Amin',
      familyName: 'Islam',
      emailVerified: true,
      provider: 'google',
      idToken: 'mock_token_' + Date.now(),
      joinedAt: new Date().toISOString()
    };
    localStorage.setItem('mytube_user', JSON.stringify(mockUser));
    return mockUser;
  }

  try {
    await loadGoogleScript();
    
    return new Promise((resolve, reject) => {
      authCallback = (user) => {
        authCallback = null;
        resolve(user);
      };

      // Trigger One Tap or Popup
      window.google.accounts.id.prompt((notification) => {
        console.log('One Tap notification:', notification);
        if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
          // Fallback to popup - render button and click programmatically
          // Or use OAuth2 token client for access token
          console.log('One Tap not displayed, using popup');
          // For now, show the Google button
          const tempDiv = document.createElement('div');
          tempDiv.style.display = 'none';
          document.body.appendChild(tempDiv);
          
          window.google.accounts.id.renderButton(tempDiv, {
            theme: 'outline',
            size: 'large',
            type: 'standard'
          });

          // Simulate click on the rendered button
          setTimeout(() => {
            const googleBtn = tempDiv.querySelector('div[role="button"]');
            if (googleBtn) googleBtn.click();
            document.body.removeChild(tempDiv);
          }, 100);
        }
      });

      // Timeout fallback
      setTimeout(() => {
        if (authCallback) {
          authCallback = null;
          reject(new Error('Google Sign-In timeout'));
        }
      }, 30000);
    });
  } catch (error) {
    console.error('Google Sign-In failed:', error);
    throw error;
  }
}

// OAuth 2.0 Access Token - For YouTube API (like, subscribe, etc.)
export async function getGoogleAccessToken() {
  if (USE_MOCK) {
    return null;
  }

  try {
    await loadGoogleScript();

    return new Promise((resolve, reject) => {
      const client = window.google.accounts.oauth2.initTokenClient({
        client_id: GOOGLE_CLIENT_ID,
        scope: 'https://www.googleapis.com/auth/youtube.readonly https://www.googleapis.com/auth/youtube.force-ssl https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email',
        callback: (response) => {
          console.log('🔑 Access Token Response:', response);
          if (response.access_token) {
            localStorage.setItem('mytube_access_token', response.access_token);
            resolve(response.access_token);
          } else {
            reject(new Error('No access token'));
          }
        },
        error_callback: (error) => {
          console.error('Access token error:', error);
          reject(error);
        }
      });

      client.requestAccessToken();
    });
  } catch (error) {
    console.error('Get access token failed:', error);
    return null;
  }
}

// Sign out
export function signOutGoogle() {
  if (USE_MOCK) {
    localStorage.removeItem('mytube_user');
    localStorage.removeItem('mytube_id_token');
    localStorage.removeItem('mytube_access_token');
    return;
  }

  try {
    if (window.google?.accounts?.id) {
      window.google.accounts.id.disableAutoSelect();
      // Revoke token if exists
      const token = localStorage.getItem('mytube_access_token');
      if (token) {
        window.google.accounts.oauth2.revoke(token, () => {
          console.log('Token revoked');
        });
      }
    }
    localStorage.removeItem('mytube_user');
    localStorage.removeItem('mytube_id_token');
    localStorage.removeItem('mytube_access_token');
    console.log('✅ Signed out from Google');
  } catch (error) {
    console.error('Sign out error:', error);
  }
}

// Render Google Sign-In Button (for custom UI)
export function renderGoogleButton(elementId, callback) {
  if (USE_MOCK) return;

  loadGoogleScript().then(() => {
    const element = document.getElementById(elementId);
    if (!element) return;

    authCallback = callback;

    window.google.accounts.id.renderButton(element, {
      theme: 'outline',
      size: 'large',
      type: 'standard',
      text: 'signin_with',
      shape: 'pill',
      logo_alignment: 'left',
      width: '300'
    });
  });
}

export const authStatus = {
  isMock: USE_MOCK,
  hasClientId: !!GOOGLE_CLIENT_ID && GOOGLE_CLIENT_ID !== 'YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com',
  clientId: GOOGLE_CLIENT_ID ? `${GOOGLE_CLIENT_ID.substring(0, 20)}...` : 'Not set'
};
