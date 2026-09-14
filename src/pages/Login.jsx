import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [isSignup, setIsSignup] = useState(false);
  const { login, googleLogin } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email || !password) return;
    login(email, name || email.split('@')[0]);
    navigate('/');
  };

  const handleGoogle = () => {
    googleLogin();
    navigate('/');
  };

  return (
    <div className="min-h-[calc(100vh-56px)] flex items-center justify-center bg-[#0f0f0f] p-4">
      <div className="w-full max-w-[450px] bg-[#212121] rounded-2xl p-8 border border-[#303030]">
        <div className="text-center mb-8">
          <div className="flex justify-center items-center gap-2 mb-4">
            <div className="bg-red-600 rounded-lg p-2">
              <svg className="w-6 h-6 text-white" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5.14v14l11-7-11-7z" /></svg>
            </div>
            <span className="text-2xl font-bold">MyTube</span>
          </div>
          <h1 className="text-2xl font-bold">{isSignup ? 'অ্যাকাউন্ট তৈরি করুন' : 'স্বাগতম!'}</h1>
          <p className="text-[#aaa] text-sm mt-2">{isSignup ? 'MyTube-এ যোগ দিন এবং শেখা শুরু করুন' : 'আপনার অ্যাকাউন্টে লগইন করুন'}</p>
        </div>

        <button onClick={handleGoogle} className="w-full flex items-center justify-center gap-3 bg-white text-black rounded-full py-3 font-medium hover:bg-[#e5e5e5] transition-colors">
          <img src="https://www.google.com/favicon.ico" className="w-5 h-5" alt="" />
          Google দিয়ে {isSignup ? 'সাইন আপ' : 'লগইন'} করুন
        </button>

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

          <button type="submit" className="w-full bg-[#3ea6ff] text-black rounded-full py-3 font-medium hover:bg-[#65b8ff] transition-colors mt-2">
            {isSignup ? 'অ্যাকাউন্ট তৈরি করুন' : 'লগইন করুন'}
          </button>
        </form>

        <p className="text-center text-sm text-[#aaa] mt-6">
          {isSignup ? 'ইতিমধ্যে অ্যাকাউন্ট আছে?' : 'অ্যাকাউন্ট নেই?'}{' '}
          <button onClick={() => setIsSignup(!isSignup)} className="text-[#3ea6ff] hover:underline">
            {isSignup ? 'লগইন করুন' : 'সাইন আপ করুন'}
          </button>
        </p>

        <div className="mt-8 p-4 bg-[#181818] rounded-xl border border-[#303030]">
          <p className="text-xs font-medium text-[#aaa]">🎓 ডেভেলপার নোট:</p>
          <p className="text-xs text-[#717171] mt-1">এটি একটি ডেমো অথ সিস্টেম। রিয়েল অ্যান্ড্রয়েড অ্যাপে Firebase Authentication ব্যবহার করবেন। বর্তমানে যেকোনো ইমেইল/পাসওয়ার্ড দিয়ে লগইন করতে পারবেন, ডাটা localStorage এ সেভ হবে।</p>
        </div>

        <div className="text-center mt-6">
          <Link to="/" className="text-sm text-[#717171] hover:text-white">← হোমে ফিরে যান</Link>
        </div>
      </div>
    </div>
  );
}
