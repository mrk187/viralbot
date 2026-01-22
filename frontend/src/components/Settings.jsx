import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { api } from '../api';

export default function Settings() {
  const { t } = useTranslation();
  const [settings, setSettings] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    try {
      const res = await api.getSettings();
      setSettings(res.data);
    } catch (error) {
      console.error('Error loading settings:', error);
    }
  };

  const handleSave = async () => {
    setLoading(true);
    setMessage('');
    try {
      await api.updateSettings(settings);
      setMessage(t('settings.saved'));
      setTimeout(() => setMessage(''), 3000);
    } catch (error) {
      setMessage(t('settings.error'));
    } finally {
      setLoading(false);
    }
  };

  if (!settings) return <div>{t('common.loading')}</div>;
  
  return (
    <div>
      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ {t('settings.scheduler')} ═══╗</div>
        <div style={{ padding: '10px' }}>
          <label>
            {t('settings.enabled')}: 
            <input 
              type="checkbox" 
              checked={settings.schedulerEnabled}
              onChange={(e) => setSettings({...settings, schedulerEnabled: e.target.checked})}
            /> {settings.schedulerEnabled ? t('settings.on') : t('settings.off')}
          </label>
          <div style={{ marginTop: '10px' }}>
            {t('settings.cronSchedule')}: 
            <input 
              type="text" 
              value={settings.cronSchedule}
              onChange={(e) => setSettings({...settings, cronSchedule: e.target.value})}
              style={{ 
              background: 'transparent', 
              border: '1px solid var(--text-primary)', 
              color: 'var(--text-primary)',
              padding: '5px',
              fontFamily: 'Fira Code'
            }} />
          </div>
        </div>
      </div>

      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ {t('settings.videoConfig')} ═══╗</div>
        <div style={{ padding: '10px' }}>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
            <label style={{ width: '120px' }}>{t('settings.duration')}:</label>
            <input 
              type="number" 
              value={settings.videoDuration}
              onChange={(e) => setSettings({...settings, videoDuration: parseInt(e.target.value)})}
              style={{ 
            background: 'transparent', 
            border: '1px solid var(--text-primary)', 
            color: 'var(--text-primary)',
            padding: '5px',
            width: '100px',
            fontFamily: 'Fira Code',
            marginRight: '10px'
          }} />
            <span>{t('settings.seconds')}</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
            <label style={{ width: '120px' }}>{t('settings.resolution')}:</label>
            <select
              value={settings.videoResolution}
              onChange={(e) => setSettings({...settings, videoResolution: e.target.value})}
              style={{ 
                background: 'transparent', 
                border: '1px solid var(--text-primary)', 
                color: 'var(--text-primary)',
                padding: '5px',
                fontFamily: 'Fira Code',
                width: '200px'
              }}
            >
              <option value="1080x1920">{t('settings.resolutionShorts')}</option>
              <option value="1920x1080">{t('settings.resolutionWide')}</option>
            </select>
          </div>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <label style={{ width: '120px' }}>{t('settings.fps')}:</label>
            <input 
              type="number" 
              value={settings.videoFps}
              onChange={(e) => setSettings({...settings, videoFps: parseInt(e.target.value)})}
              style={{ 
                background: 'transparent', 
                border: '1px solid var(--text-primary)', 
                color: 'var(--text-primary)',
                padding: '5px',
                width: '100px',
                fontFamily: 'Fira Code'
              }}
            />
          </div>
        </div>
      </div>

      {message && (
        <div style={{ marginTop: '10px', color: message.includes('success') ? 'var(--text-accent)' : 'var(--text-error)' }}>
          {message}
        </div>
      )}
      
      <button 
        className="button-primary" 
        onClick={handleSave}
        disabled={loading}
      >
        [{loading ? 'SAVING...' : t('settings.saveChanges')}]
      </button>
    </div>
  );
}
