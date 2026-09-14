import { createContext, useContext, useState, useEffect } from 'react';
import { initGoogleAuth, signInWithGoogle as realGoogleSignIn, signOutGoogle, getGoogleAccessToken, authStatus } from '../services/googleAuth';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [accessToken, setAccessToken] = useState(null);

  useEffect(() => {
    // Load saved user
    const savedUser = localStorage.getItem('mytube_user');
    const savedToken = localStorage.getItem('mytube_access_token');
    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser));
        if (savedToken) setAccessToken(savedToken);
      } catch (e) {
        console.error('Failed to parse saved user', e);
      }
    }
    
    // Initialize Google Auth (Real or Mock)
    initGoogleAuth().then(result => {
      console.log('🔐 Auth initialized:', result);
      setLoading(false);
    });
  }, []);

  const login = (email, name) => {
    const userData = {
      id: 'email_' + Date.now(),
      email,
      name: name || email.split('@')[0],
      avatar: `https://i.pravatar.cc/100?u=${email}`,
      provider: 'email',
      joinedAt: new Date().toISOString()
    };
    setUser(userData);
    localStorage.setItem('mytube_user', JSON.stringify(userData));
    console.log('✅ Email login:', userData);
    return userData;
  };

  const logout = () => {
    signOutGoogle();
    setUser(null);
    setAccessToken(null);
    console.log('✅ Logged out');
  };

  const googleLogin = async () => {
    try {
      setLoading(true);
      const googleUser = await realGoogleSignIn();
      setUser(googleUser);
      console.log('✅ Google login success:', googleUser);
      
      // Try to get access token for YouTube API
      if (!authStatus.isMock) {
        try {
          const token = await getGoogleAccessToken();
          if (token) {
            setAccessToken(token);
            console.log('🔑 Access token obtained for YouTube API');
          }
        } catch (e) {
          console.warn('Could not get access token (optional):', e);
        }
      }
      
      return googleUser;
    } catch (error) {
      console.error('Google login failed:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const getAccessTokenForApi = async () => {
    if (accessToken) return accessToken;
    
    if (!authStatus.isMock && user?.provider === 'google') {
      try {
        const token = await getGoogleAccessToken();
        if (token) {
          setAccessToken(token);
          return token;
        }
      } catch (e) {
        console.error('Failed to get access token:', e);
      }
    }
    return null;
  };

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      logout, 
      googleLogin, 
      loading,
      accessToken,
      getAccessTokenForApi,
      isRealAuth: !authStatus.isMock,
      authStatus
    }}>
      {children}
    </AuthContext.Provider>
  );
};
