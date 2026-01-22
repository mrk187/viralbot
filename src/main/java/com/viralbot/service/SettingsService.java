package com.viralbot.service;

import com.viralbot.config.ViralBotConfig;
import com.viralbot.model.AppSettings;
import com.viralbot.repository.SettingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SettingsService {
    
    private static final Logger log = Logger.getLogger(SettingsService.class.getName());
    
    private final SettingsRepository settingsRepository;
    private final ViralBotConfig config;
    
    @PostConstruct
    public void initializeSettings() {
        if (settingsRepository.count() == 0) {
            AppSettings defaults = AppSettings.builder()
                .schedulerEnabled(config.getSchedule().isEnabled())
                .cronSchedule(config.getSchedule().getCron())
                .videoDuration(config.getVideo().getDuration())
                .videoResolution(config.getVideo().getResolution())
                .videoFps(config.getVideo().getFps())
                .build();
            settingsRepository.save(defaults);
            log.info("Initialized default settings from application.properties");
        }
        loadSettingsToConfig();
    }
    
    public AppSettings getSettings() {
        return settingsRepository.findFirstByOrderByIdAsc()
            .orElseThrow(() -> new RuntimeException("Settings not found"));
    }
    
    public AppSettings updateSettings(AppSettings settings) {
        AppSettings existing = getSettings();
        existing.setSchedulerEnabled(settings.isSchedulerEnabled());
        existing.setCronSchedule(settings.getCronSchedule());
        existing.setVideoDuration(settings.getVideoDuration());
        existing.setVideoResolution(settings.getVideoResolution());
        existing.setVideoFps(settings.getVideoFps());
        
        AppSettings saved = settingsRepository.save(existing);
        loadSettingsToConfig();
        log.info("Settings updated and applied to config");
        return saved;
    }
    
    private void loadSettingsToConfig() {
        AppSettings settings = getSettings();
        config.getSchedule().setEnabled(settings.isSchedulerEnabled());
        config.getSchedule().setCron(settings.getCronSchedule());
        config.getVideo().setDuration(settings.getVideoDuration());
        config.getVideo().setResolution(settings.getVideoResolution());
        config.getVideo().setFps(settings.getVideoFps());
        log.info("Loaded settings from database to config");
    }
}
