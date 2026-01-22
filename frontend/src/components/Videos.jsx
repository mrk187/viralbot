import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { api } from '../api';

export default function Videos() {
  const { t } = useTranslation();
  const [videos, setVideos] = useState([]);

  useEffect(() => {
    loadVideos();
  }, []);

  const loadVideos = async () => {
    try {
      const res = await api.getVideos();
      setVideos(res.data);
    } catch (error) {
      console.error('Error loading videos:', error);
    }
  };

  const handleDelete = async (id) => {
    if (confirm(t('videos.confirmDelete'))) {
      try {
        await api.deleteVideo(id);
        loadVideos();
      } catch (error) {
        alert(t('videos.deleteError'));
      }
    }
  };

  return (
    <div>
      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ {t('videos.library')} ({t('videos.videosCount', { count: videos.length })}) ═══╗</div>
      </div>

      <div className="video-grid">
        {videos.map(video => (
          <div key={video.id} className="video-card">
            <div className="video-title">&gt; {video.title}</div>
            <div style={{ fontSize: '0.9rem', marginBottom: '10px' }}>
              {t('videos.channel')}: {video.channel} | {new Date(video.createdAt).toLocaleString()}
            </div>
            
            {video.youtubeUrl && (
              <a href={video.youtubeUrl} target="_blank" rel="noopener noreferrer" className="video-link">
                [✓] {t('videos.youtube')}: {video.youtubeUrl}
              </a>
            )}
            
            <div style={{ marginTop: '10px' }}>
              <button className="button-primary" onClick={() => handleDelete(video.id)}>
                [{t('videos.delete')}]
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
