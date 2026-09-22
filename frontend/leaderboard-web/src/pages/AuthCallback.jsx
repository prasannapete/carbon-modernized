import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { exchangeCode } from '../lib/auth';

// Guards against exchanging the same one-time code twice (double effect / re-mount).
const exchangedCodes = new Set();

export default function AuthCallback() {
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      const code = new URL(window.location.href).searchParams.get('code');
      if (!code) {
        navigate('/', { replace: true });
        return;
      }
      if (exchangedCodes.has(code)) return;
      exchangedCodes.add(code);
      try {
        await exchangeCode(code);
      } catch (e) {
        console.error('code <-> token failed:', e);
      }
      navigate('/', { replace: true });
    })();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  return null;
}
