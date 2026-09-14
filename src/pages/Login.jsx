import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authStatus, renderGoogleButton } from '../services/googleAuth';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [isSignup, setIsSignup] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login, googleLogin, isRealAuth } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    // Render real Google button if available
    if (!authStatus.isMock) {
      renderGoogleButton('google-signin-btn', (user) => {
        console.log('Google button login:', user);
        navigate('/');
      });
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!email || !password) {
      setError('ইমেইল এবং পাসওয়ার্ড দিন');
      return;
    }
    try {
      setLoading(true);
      login(email, name || email.split('@')[0]);
      navigate('/');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGoogle = async () => {
    try {
      setLoading(true);
      setError('');
      await googleLogin();
      navigate('/');
    } catch (err) {
      setError('Google Login failed: ' + err.message);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-56px)] flex items-center justify-center bg-[#0f0f0f] p-4">
      <div className="w-full max-w-[480px] bg-[#212121] rounded-2xl p-8 border border-[#303030]">
        <div className="text-center mb-8">
          <div className="flex justify-center items-center gap-2 mb-4">
            <div className="bg-red-600 rounded-lg p-2">
              <svg className="w-6 h-6 text-white" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5.14v14l11-7-11-7z" /></svg>
            </div>
            <span className="text-2xl font-bold">MyTube</span>
            <span className={`text-xs px-2 py-1 rounded-full ml-2 ${isRealAuth ? 'bg-green-900/30 text-green-400 border border-green-800' : 'bg-yellow-900/30 text-yellow-400 border border-yellow-800'}`}>
              {isRealAuth ? 'Real OAuth' : 'Mock Auth'}
            </span>
          </div>
          <h1 className="text-2xl font-bold">{isSignup ? 'অ্যাকাউন্ট তৈরি করুন' : 'স্বাগতম!'}</h1>
          <p className="text-[#aaa] text-sm mt-2">{isSignup ? 'MyTube-এ যোগ দিন এবং শেখা শুরু করুন' : 'আপনার অ্যাকাউন্টে লগইন করুন'}</p>
        </div>

        {/* Real Google Button Container */}
        <div className="space-y-3">
          <div id="google-signin-btn" className="flex justify-center min-h-[44px]"></div>
          
          <button 
            onClick={handleGoogle} 
            disabled={loading}
            className="w-full flex items-center justify-center gap-3 bg-white text-black rounded-full py-3 font-medium hover:bg-[#e5e5e5] transition-colors disabled:opacity-50"
          >
            <img src="https://www.google.com/favicon.ico" className="w-5 h-5" alt="" />
            {loading ? 'লোড হচ্ছে...' : `Google দিয়ে ${isSignup ? 'সাইন আপ' : 'লগইন'} করুন`}
          </button>
          
          <div className="bg-[#181818] border border-[#303030] rounded-lg p-3 text-xs">
            <p className="font-medium text-[#aaa]">🔐 Google Auth কিভাবে কাজ করে:</p>
            <pre className="mt-2 text-[11px] text-[#717171] whitespace-pre-wrap">
{`1. accounts.google.com/gsi/client লোড
2. google.accounts.id.initialize({
     client_id: "${authStatus.clientId}",
     callback: (response) => {
       // response.credential = JWT ID Token
       const user = decodeJwt(token)
     }
   })
3. OAuth2 Token (YouTube API):
   google.accounts.oauth2.initTokenClient({
     scope: "youtube.readonly youtube.force-ssl"
   })`}
            </pre>
          </div>
        </div>

        <div className="flex items-center gap-4 my-6">
          <div className="flex-1 h-[1px] bg-[#303030]" />
          <span className="text-[#717171] text-sm">অথবা</span>
          <div className="flex-1 h-[1px] bg-[#303030]" />
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {isSignup && (
            <div>
              <label className="text-sm text-[#aaa]">নাম</label>
              <input
                value={name}
                onChange={e => setName(e.target.value)}
                placeholder="আমিন ইসলাম"
                className="w-full mt-1 bg-[#121212] border border-[#303030] rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-[#3ea6ff]"
              />
            </div>
          )}
          <div>
            <label className="text-sm text-[#aaa]">ইমেইল</label>
            <input
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              placeholder="amin@example.com"
              className="w-full mt-1 bg-[#121212] border border-[#303030] rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-[#3ea6ff]"
              required
            />
          </div>
          <div>
            <label className="text-sm text-[#aaa]">পাসওয়ার্ড</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="••••••••"
              className="w-full mt-1 bg-[#121212] border border-[#303030] rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-[#3ea6ff]"
              required
            />
          </div>

          {error && (
            <div className="bg-red-900/20 border border-red-800/50 text-red-400 text-sm p-3 rounded-lg">
              {error}
            </div>
          )}

          <button type="submit" disabled={loading} className="w-full bg-[#3ea6ff] text-black rounded-full py-3 font-medium hover:bg-[#65b8ff] transition-colors mt-2 disabled:opacity-50">
            {loading ? 'প্রসেস হচ্ছে...' : (isSignup ? 'অ্যাকাউন্ট তৈরি করুন' : 'লগইন করুন')}
          </button>
        </form>

        <p className="text-center text-sm text-[#aaa] mt-6">
          {isSignup ? 'ইতিমধ্যে অ্যাকাউন্ট আছে?' : 'অ্যাকাউন্ট নেই?'}{' '}
          <button onClick={() => setIsSignup(!isSignup)} className="text-[#3ea6ff] hover:underline">
            {isSignup ? 'লগইন করুন' : 'সাইন আপ করুন'}
          </button>
        </p>

        <div className="mt-8 p-4 bg-[#181818] rounded-xl border border-[#303030]">
          <p className="text-xs font-medium text-[#aaa]">🎓 অ্যান্ড্রয়েডে কিভাবে করবে:</p>
          <pre className="text-[11px] text-[#717171] mt-2 whitespace-pre-wrap">
{`// Android - Firebase Auth + Google Sign-In
val gso = GoogleSignInOptions.Builder()
  .requestIdToken(getString(R.string.web_client_id))
  .requestEmail()
  .build()

val googleClient = GoogleSignIn.getClient(this, gso)
startActivityForResult(googleClient.signInIntent, RC_SIGN_IN)

// Firebase
val credential = GoogleAuthProvider.getCredential(idToken, null)
auth.signInWithCredential(credential)`}
          </pre>
        </div>

        <div className="text-center mt-6">
          <Link to="/" className="text-sm text-[#717171] hover:text-white">← হোমে ফিরে যান</Link>
        </div>
      </div>
    </div>
  );
}
