import React from 'react';
import { useNavigate } from 'react-router-dom';

function Navbar({ user, onLogout }) {
  const navigate = useNavigate();
  return (
    <nav className="glass-panel" style={{ borderRadius: 0, padding: '16px 32px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: 'none', borderLeft: 'none', borderRight: 'none' }}>
      <div style={{ fontWeight: 800, fontSize: '1.5rem', background: 'linear-gradient(135deg, var(--primary), var(--secondary))', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', cursor: 'pointer' }} onClick={() => navigate('/')}>
        TrustPay
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        <span style={{ fontWeight: 600 }}>Hi, {user.name}</span>
        <button className="btn btn-secondary" onClick={onLogout} style={{ padding: '6px 12px' }}>Logout</button>
      </div>
    </nav>
  );
}

export default Navbar;
