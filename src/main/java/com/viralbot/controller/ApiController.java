package com.viralbot.controller;

import com.viralbot.dto.QuotaUsageDTO;
import com.viralbot.dto.SystemStatusDTO;
import com.viralbot.model.Video;
import com.viralbot.repository.VideoRepository;
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
@CrossOrigin(origins = "*")
public class ApiController {
    
    private final ViralBotOrchestrator orchestrator;
    private final VideoRepository videoRepository;
    
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
            .nextScheduledRun("09:00:00 AM")
            .schedulerEnabled(true)
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
                .name("Gemini API").used(48).limit(60).percentage(80.0).unit("req/min").build())
            .googleTts(QuotaUsageDTO.QuotaInfo.builder()
                .name("Google TTS").used(10000).limit(33000).percentage(30.0).unit("chars/day").build())
            .pexels(QuotaUsageDTO.QuotaInfo.builder()
                .name("Pexels API").used(120).limit(200).percentage(60.0).unit("req/hour").build())
            .youtube(QuotaUsageDTO.QuotaInfo.builder()
                .name("YouTube API").used(2000).limit(10000).percentage(20.0).unit("units/day").build())
            .tiktok(QuotaUsageDTO.QuotaInfo.builder()
                .name("TikTok API").used(15).limit(50).percentage(30.0).unit("uploads/day").build())
            .build();
        
        return ResponseEntity.ok(quota);
    }
}
