import './i18n';
import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import Dashboard from './components/Dashboard';
import Videos from './components/Videos';
import Logs from './components/Logs';
import Settings from './components/Settings';
import './terminal.css';

import { useTranslation } from 'react-i18next';

function Navigation() {
  const location = useLocation();
  const { t } = useTranslation();
  
  return (
    <div className="terminal-header">
      <div className="terminal-title" style={{ fontSize: '2rem', fontWeight: 'bold', letterSpacing: '0.1em' }}>
        {t('app.title')}
      </div>
      <div style={{ fontSize: '0.9rem', marginTop: '10px' }}>
        &gt; {t('app.subtitle')}
      </div>
      <nav className="terminal-nav">
        <Link to="/">
          <button className={`nav-button ${location.pathname === '/' ? 'active' : ''}`}>
            [{t('nav.dashboard')}]
          </button>
        </Link>
        <Link to="/videos">
          <button className={`nav-button ${location.pathname === '/videos' ? 'active' : ''}`}>
            [{t('nav.videos')}]
          </button>
        </Link>
        <Link to="/logs">
          <button className={`nav-button ${location.pathname === '/logs' ? 'active' : ''}`}>
            [{t('nav.logs')}]
          </button>
        </Link>
        <Link to="/settings">
          <button className={`nav-button ${location.pathname === '/settings' ? 'active' : ''}`}>
            [{t('nav.settings')}]
          </button>
        </Link>
      </nav>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <div className="terminal-container">
        <Navigation />
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/videos" element={<Videos />} />
          <Route path="/logs" element={<Logs />} />
          <Route path="/settings" element={<Settings />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
