import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedUser = localStorage.getItem('mytube_user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const login = (email, name) => {
    const userData = {
      email,
      name: name || email.split('@')[0],
      avatar: `https://i.pravatar.cc/100?u=${email}`,
      joinedAt: new Date().toISOString()
    };
    setUser(userData);
    localStorage.setItem('mytube_user', JSON.stringify(userData));
    return userData;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('mytube_user');
  };

  const googleLogin = () => {
    // Mock Google login
    const mockGoogleUser = {
      email: 'amin@gmail.com',
      name: 'Amin Islam',
      avatar: 'https://i.pravatar.cc/100?img=8',
      provider: 'google',
      joinedAt: new Date().toISOString()
    };
    setUser(mockGoogleUser);
    localStorage.setItem('mytube_user', JSON.stringify(mockGoogleUser));
    return mockGoogleUser;
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, googleLogin, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
