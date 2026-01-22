package com.viralbot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "scheduler_enabled", nullable = false)
    private boolean schedulerEnabled;
    
    @Column(name = "cron_schedule", nullable = false)
    private String cronSchedule;
    
    @Column(name = "video_duration", nullable = false)
    private int videoDuration;
    
    @Column(name = "video_resolution", nullable = false)
    private String videoResolution;
    
    @Column(name = "video_fps", nullable = false)
    private int videoFps;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
