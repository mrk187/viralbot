import { useState, useEffect } from 'react';

export default function Logs() {
  const [logs, setLogs] = useState([
    { level: 'INFO', message: 'System initialized', timestamp: Date.now() },
    { level: 'INFO', message: 'Waiting for commands...', timestamp: Date.now() }
  ]);

  return (
    <div>
      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ SYSTEM LOGS ═══╗</div>
        <div style={{ maxHeight: '600px', overflowY: 'auto' }}>
          {logs.map((log, idx) => (
            <div key={idx} className={`log-entry log-${log.level.toLowerCase()}`}>
              [{new Date(log.timestamp).toLocaleTimeString()}] [{log.level}] {log.message}
            </div>
          ))}
          <div className="cursor-blink">█</div>
        </div>
      </div>
    </div>
  );
}
