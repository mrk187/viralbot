package com.viralbot.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String channel;
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(length = 5000)
    private String scriptContent;
    
    private String audioFilePath;
    
    @ElementCollection
    private List<String> imageUrls;
    
    private String videoFilePath;
    private long durationSeconds;
    
    private String youtubeVideoId;
    private String youtubeUrl;

    private LocalDateTime createdAt;
    private LocalDateTime uploadedAt;
    
    private String status;
}
