import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { api } from '../api';

export default function Logs() {
  const { t } = useTranslation();
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    loadLogs();
    const interval = setInterval(loadLogs, 5000);
    return () => clearInterval(interval);
  }, []);

  const loadLogs = async () => {
    try {
      const res = await api.getLogs();
      setLogs(res.data);
    } catch (error) {
      console.error('Error loading logs:', error);
    }
  };

  return (
    <div>
      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ {t('logs.title')} ═══╗</div>
        <div style={{ maxHeight: '600px', overflowY: 'auto' }}>
          {logs.length === 0 ? (
            <div className="log-entry log-info">
              [{new Date().toLocaleTimeString()}] [{t('logs.level.info')}] No logs available
            </div>
          ) : (
            logs.map((log, idx) => (
              <div key={idx} className={`log-entry log-${log.level.toLowerCase()}`}>
                [{new Date(log.createdAt).toLocaleTimeString()}] [{log.level}] {log.message}
              </div>
            ))
          )}
          <div className="cursor-blink">█</div>
        </div>
      </div>
    </div>
  );
}
