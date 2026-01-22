package com.viralbot.controller;

import com.viralbot.config.ViralBotConfig;
import com.viralbot.dto.QuotaUsageDTO;
import com.viralbot.dto.SystemStatusDTO;
import com.viralbot.model.AppSettings;
import com.viralbot.model.LogEntry;
import com.viralbot.model.Video;
import com.viralbot.repository.VideoRepository;
import com.viralbot.service.LogService;
import com.viralbot.service.SettingsService;
import com.viralbot.service.ViralBotOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class ApiController {
    
    private final ViralBotOrchestrator orchestrator;
    private final VideoRepository videoRepository;
    private final ViralBotConfig config;
    private final LogService logService;
    private final SettingsService settingsService;
    
    @GetMapping("/channels")
    public ResponseEntity<Map<String, Object>> getChannels() {
        List<Map<String, String>> channels = config.getChannels().stream()
            .map(channel -> Map.of(
                "name", channel,
                "description", config.getChannel().get(channel).getDescription()
            ))
            .toList();
        return ResponseEntity.ok(Map.of("channels", channels));
    }
    
    @GetMapping("/status")
    public ResponseEntity<SystemStatusDTO> getStatus() {
        List<Video> allVideos = videoRepository.findAll();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        long todayCount = allVideos.stream()
            .filter(v -> v.getCreatedAt().isAfter(today))
            .count();
        
        long successCount = allVideos.stream()
            .filter(v -> "SUCCESS".equals(v.getStatus()))
            .count();
        
        SystemStatusDTO status = SystemStatusDTO.builder()
            .videosGeneratedToday((int) todayCount)
            .videosGeneratedThisWeek(allVideos.size())
            .videosGeneratedThisMonth(allVideos.size())
            .successfulUploads((int) successCount)
            .failedUploads(allVideos.size() - (int) successCount)
            .successRate(allVideos.isEmpty() ? 0 : (double) successCount / allVideos.size() * 100)
            .currentTask("Idle")
            .nextScheduledRun(config.getSchedule().getCron())
            .schedulerEnabled(config.getSchedule().isEnabled())
            .build();
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getVideos() {
        return ResponseEntity.ok(videoRepository.findByOrderByCreatedAtDesc());
    }
    
    @GetMapping("/videos/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable Long id) {
        return videoRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/generate/{channel}")
    public ResponseEntity<Map<String, String>> generateVideo(@PathVariable String channel) {
        new Thread(() -> orchestrator.processChannel(channel)).start();
        return ResponseEntity.ok(Map.of("message", "Video generation started for channel: " + channel));
    }
    
    @PostMapping("/generate/all")
    public ResponseEntity<Map<String, String>> generateAllVideos() {
        new Thread(() -> orchestrator.processAllChannels()).start();
        return ResponseEntity.ok(Map.of("message", "Video generation started for all channels"));
    }
    
    @GetMapping("/quota")
    public ResponseEntity<QuotaUsageDTO> getQuotaUsage() {
        QuotaUsageDTO quota = QuotaUsageDTO.builder()
            .gemini(QuotaUsageDTO.QuotaInfo.builder()
                .name("Gemini API").used(0).limit(60).percentage(0.0).unit("req/min").build())
            .googleTts(QuotaUsageDTO.QuotaInfo.builder()
                .name("Google TTS").used(0).limit(33000).percentage(0.0).unit("chars/day").build())
            .pexels(QuotaUsageDTO.QuotaInfo.builder()
                .name("Pexels API").used(0).limit(200).percentage(0.0).unit("req/hour").build())
            .youtube(QuotaUsageDTO.QuotaInfo.builder()
                .name("YouTube API").used(0).limit(10000).percentage(0.0).unit("units/day").build())
            .build();
        
        return ResponseEntity.ok(quota);
    }
    
    @GetMapping("/logs")
    public ResponseEntity<List<LogEntry>> getLogs() {
        return ResponseEntity.ok(logService.getRecentLogs());
    }
    
    @GetMapping("/settings")
    public ResponseEntity<AppSettings> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }
    
    @PutMapping("/settings")
    public ResponseEntity<AppSettings> updateSettings(@RequestBody AppSettings settings) {
        return ResponseEntity.ok(settingsService.updateSettings(settings));
    }

}
