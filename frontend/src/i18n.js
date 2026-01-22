import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

const resources = {
  en: {
    translation: {
      app: {
        title: 'VIRALBOT',
        subtitle: 'Automated Content Generation System v1.0.0'
      },
      nav: {
        dashboard: 'DASHBOARD',
        videos: 'VIDEOS',
        logs: 'LOGS',
        settings: 'SETTINGS'
      },
      dashboard: {
        systemStatus: 'SYSTEM STATUS',
        videosToday: 'Videos Today',
        successRate: 'Success Rate',
        successful: 'Successful',
        failed: 'Failed',
        channels: 'CHANNELS',
        generateNow: 'Generate Now',
        generateAll: 'GENERATE ALL CHANNELS',
        generating: 'GENERATING...',
        generatingAll: 'GENERATING ALL...',
        apiQuota: 'API QUOTA USAGE'
      },
      videos: {
        library: 'VIDEO LIBRARY',
        videosCount: '{{count}} videos',
        channel: 'Channel',
        youtube: 'YouTube',
        delete: 'DELETE',
        confirmDelete: 'Delete this video?',
        deleteError: 'Error deleting video'
      },
      logs: {
        title: 'SYSTEM LOGS',
        level: {
          info: 'INFO',
          warn: 'WARN',
          error: 'ERROR',
          debug: 'DEBUG'
        }
      },
      settings: {
        scheduler: 'SCHEDULER',
        enabled: 'Enabled',
        on: 'ON',
        off: 'OFF',
        cronSchedule: 'Cron Schedule',
        videoConfig: 'VIDEO CONFIGURATION',
        duration: 'Duration',
        seconds: 'seconds',
        resolution: 'Resolution',
        resolutionShorts: 'Shorts (1080x1920)',
        resolutionWide: 'Wide (1920x1080)',
        fps: 'FPS',
        saveChanges: 'SAVE CHANGES',
        saved: 'Settings saved successfully',
        error: 'Failed to save settings'
      },
      common: {
        loading: 'Loading...'
      }
    }
  }
};

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: 'en',
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false
    }
  });

export default i18n;
