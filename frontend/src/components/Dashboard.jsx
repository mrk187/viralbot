import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { api } from '../api';

export default function Dashboard() {
  const { t } = useTranslation();
  const [status, setStatus] = useState(null);
  const [quota, setQuota] = useState(null);
  const [channels, setChannels] = useState([]);
  const [loading, setLoading] = useState({});

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 5000);
    return () => clearInterval(interval);
  }, []);

  const loadData = async () => {
    try {
      const [statusRes, quotaRes, channelsRes] = await Promise.all([
        api.getStatus(),
        api.getQuota(),
        api.getChannels()
      ]);
      setStatus(statusRes.data);
      setQuota(quotaRes.data);
      setChannels(channelsRes.data.channels);
    } catch (error) {
      console.error('Error loading data:', error);
    }
  };

  const handleGenerate = async (channel) => {
    setLoading(prev => ({ ...prev, [channel]: true }));
    try {
      await api.generateVideo(channel);
      setTimeout(() => setLoading(prev => ({ ...prev, [channel]: false })), 3000);
    } catch (error) {
      setLoading(prev => ({ ...prev, [channel]: false }));
    }
  };

  const handleGenerateAll = async () => {
    setLoading(prev => ({ ...prev, all: true }));
    try {
      await api.generateAll();
      setTimeout(() => setLoading(prev => ({ ...prev, all: false })), 3000);
    } catch (error) {
      setLoading(prev => ({ ...prev, all: false }));
    }
  };

  if (!status || !quota) return <div>{t('common.loading')}</div>;

  return (
    <div>
      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ {t('dashboard.systemStatus')} ═══╗</div>
        <div className="stat-grid">
          <div className="stat-item">
            <div className="stat-label">&gt; {t('dashboard.videosToday')}:</div>
            <div className="stat-value">{status.videosGeneratedToday}</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">&gt; {t('dashboard.successRate')}:</div>
            <div className="stat-value">{status.successRate.toFixed(1)}%</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">&gt; {t('dashboard.successful')}:</div>
            <div className="stat-value">{status.successfulUploads}</div>
          </div>
          <div className="stat-item">
            <div className="stat-label">&gt; {t('dashboard.failed')}:</div>
            <div className="stat-value">{status.failedUploads}</div>
          </div>
        </div>
      </div>

      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ {t('dashboard.channels')} ═══╗</div>
        {channels.map(channel => (
          <button 
            key={channel.name} 
            className="button-primary" 
            onClick={() => handleGenerate(channel.name)}
            disabled={loading[channel.name]}
          >
            {loading[channel.name] ? `[⟳] ${t('dashboard.generating')}` : `[●] ${channel.name.toUpperCase()} - ${t('dashboard.generateNow')}`}
          </button>
        ))}
        <button 
          className="button-primary" 
          onClick={handleGenerateAll}
          disabled={loading.all}
        >
          {loading.all ? `[⟳] ${t('dashboard.generatingAll')}` : `[▶] ${t('dashboard.generateAll')}`}
        </button>
      </div>

      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ {t('dashboard.apiQuota')} ═══╗</div>
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
