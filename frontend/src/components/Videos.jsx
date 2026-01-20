import { useState, useEffect } from 'react';
import { api } from '../api';

export default function Videos() {
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
    if (confirm('Delete this video?')) {
      try {
        await api.deleteVideo(id);
        loadVideos();
      } catch (error) {
        alert('Error deleting video');
      }
    }
  };

  return (
    <div>
      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ VIDEO LIBRARY ({videos.length} videos) ═══╗</div>
      </div>

      <div className="video-grid">
        {videos.map(video => (
          <div key={video.id} className="video-card">
            <div className="video-title">&gt; {video.title}</div>
            <div style={{ fontSize: '0.9rem', marginBottom: '10px' }}>
              Channel: {video.channel} | {new Date(video.createdAt).toLocaleString()}
            </div>
            
            {video.youtubeUrl && (
              <a href={video.youtubeUrl} target="_blank" rel="noopener noreferrer" className="video-link">
                [✓] YouTube: {video.youtubeUrl}
              </a>
            )}
            
            {video.tiktokUrl && (
              <a href={video.tiktokUrl} target="_blank" rel="noopener noreferrer" className="video-link">
                [✓] TikTok: {video.tiktokUrl}
              </a>
            )}
            
            <div style={{ marginTop: '10px' }}>
              <button className="button-primary" onClick={() => handleDelete(video.id)}>
                [DELETE]
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
