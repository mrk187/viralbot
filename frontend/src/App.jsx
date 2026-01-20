import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import Dashboard from './components/Dashboard';
import Videos from './components/Videos';
import Logs from './components/Logs';
import Settings from './components/Settings';
import './terminal.css';

function Navigation() {
  const location = useLocation();
  
  return (
    <div className="terminal-header">
      <div className="terminal-title" style={{ fontSize: '2rem', fontWeight: 'bold', letterSpacing: '0.1em' }}>
        VIRALBOT
      </div>
      <div style={{ fontSize: '0.9rem', marginTop: '10px' }}>
        &gt; Automated Content Generation System v1.0.0
      </div>
      <nav className="terminal-nav">
        <Link to="/">
          <button className={`nav-button ${location.pathname === '/' ? 'active' : ''}`}>
            [DASHBOARD]
          </button>
        </Link>
        <Link to="/videos">
          <button className={`nav-button ${location.pathname === '/videos' ? 'active' : ''}`}>
            [VIDEOS]
          </button>
        </Link>
        <Link to="/logs">
          <button className={`nav-button ${location.pathname === '/logs' ? 'active' : ''}`}>
            [LOGS]
          </button>
        </Link>
        <Link to="/settings">
          <button className={`nav-button ${location.pathname === '/settings' ? 'active' : ''}`}>
            [SETTINGS]
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
