import { useState, useEffect } from 'react';
import { api } from '../api';

export default function Dashboard() {
  const [status, setStatus] = useState(null);
  const [quota, setQuota] = useState(null);

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 5000);
    return () => clearInterval(interval);
  }, []);

  const loadData = async () => {
    try {
      const [statusRes, quotaRes] = await Promise.all([
        api.getStatus(),
        api.getQuota()
      ]);
      setStatus(statusRes.data);
      setQuota(quotaRes.data);
    } catch (error) {
      console.error('Error loading data:', error);
    }
  };

  const handleGenerate = async (channel) => {
    try {
      await api.generateVideo(channel);
      alert(`Started generating video for ${channel}`);
    } catch (error) {
      alert('Error starting generation');
    }
  };

  const handleGenerateAll = async () => {
    try {
      await api.generateAll();
      alert('Started generating videos for all channels');
    } catch (error) {
      alert('Error starting generation');
    }
  };

  if (!status || !quota) return <div>Loading...</div>;

  return (
    <div>
      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ SYSTEM STATUS ═══╗</div>
        <div className="stat-grid">
          <div className="stat-item">
            <div className="stat-label">&gt; Videos Today:</div>
            <div className="stat-value">{status.videosGeneratedToday}</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">&gt; Success Rate:</div>
            <div className="stat-value">{status.successRate.toFixed(1)}%</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">&gt; Successful:</div>
            <div className="stat-value">{status.successfulUploads}</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">&gt; Failed:</div>
            <div className="stat-value">{status.failedUploads}</div>
          </div>
        </div>
      </div>

      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ CHANNELS ═══╗</div>
        <button className="button-primary" onClick={() => handleGenerate('tech')}>
          [●] TECH - Generate Now
        </button>
        <button className="button-primary" onClick={() => handleGenerate('motivation')}>
          [●] MOTIVATION - Generate Now
        </button>
        <button className="button-primary" onClick={() => handleGenerate('facts')}>
          [●] FACTS - Generate Now
        </button>
        <button className="button-primary" onClick={handleGenerateAll}>
          [▶] GENERATE ALL CHANNELS
        </button>
      </div>

      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ API QUOTA USAGE ═══╗</div>
        {Object.entries(quota).map(([key, info]) => (
          <div key={key} style={{ marginBottom: '15px' }}>
            <div>{info.name} [{info.used}/{info.limit} {info.unit}]</div>
            <div className="progress-bar">
              <div className="progress-fill" style={{ width: `${info.percentage}%` }}></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
