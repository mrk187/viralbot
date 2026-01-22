package com.viralbot.service;

import com.viralbot.config.ViralBotConfig;
import com.viralbot.model.Video;
import com.viralbot.model.VideoScene;
import com.viralbot.platform.YouTubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ViralBotOrchestrator {
    
    private static final Logger log = Logger.getLogger(ViralBotOrchestrator.class.getName());
    
    private final ViralBotConfig config;
    private final ContentGeneratorService contentGenerator;
    private final TextToSpeechService ttsService;
    private final MediaService mediaService;
    private final VideoCreationService videoCreationService;
    private final YouTubeService youtubeService;
    private final LogService logService;

    @Scheduled(cron = "${viralbot.schedule.cron}")
    public void executeScheduledTask() {
        if (config.getSchedule().isEnabled()) {
            log.info("Starting scheduled video generation and upload");
            logService.info("Starting scheduled video generation and upload", null);
            processAllChannels();
        }
    }
    
    public void processAllChannels() {
        for (String channel : config.getChannels()) {
            try {
                processChannel(channel);
            } catch (Exception e) {
                log.severe("Error processing channel: " + channel + " - " + e.getMessage());
                logService.error("Error processing channel: " + e.getMessage(), channel);
            }
        }
    }
    
    public void processChannel(String channel) {
        log.info("Processing channel: " + channel);
        logService.info("Processing channel: " + channel, channel);
        
        int duration = config.getVideo().getDuration();
        String channelDescription = config.getChannel().get(channel).getDescription();
        
        // Generate script
        String script = contentGenerator.generateScript(channel, channelDescription, duration);
        log.info("Generated script: " + script.length() + " chars");
        logService.info("Generated script: " + script.length() + " chars", channel);
        
        // Generate audio
        String audioFileName = String.format("%s_%s", channel, UUID.randomUUID());
        String audioPath = ttsService.generateAudio(script, audioFileName);
        logService.info("Generated audio - " + script.length() + " chars used", channel);
        
        // Fetch background video
        String videoUrl = mediaService.fetchBackgroundVideo(channel);
        
        if (videoUrl == null) {
            logService.error("Failed to fetch background video", channel);
            throw new RuntimeException("Failed to fetch background video for channel: " + channel);
        }
        logService.info("Fetched background video", channel);
        
        // Create video
        String videoFileName = String.format("%s_%s", channel, UUID.randomUUID());
        String videoPath = videoCreationService.createVideoFromBackgroundVideo(audioPath, videoUrl, videoFileName, duration);
        logService.info("Created video file", channel);
        
        Video video = Video.builder()
            .channel(channel)
            .title(generateTitle(channel))
            .description(script.substring(0, Math.min(script.length(), 200)))
            .scriptContent(script)
            .audioFilePath(audioPath)
            .videoFilePath(videoPath)
            .durationSeconds(duration)
            .build();
        
        uploadToPlatforms(video);
        
        log.info("Successfully processed channel: " + channel);
        logService.info("Successfully processed channel", channel);
    }
    
    private void uploadToPlatforms(Video video) {
        try {
            String youtubeId = youtubeService.uploadShort(
                video.getVideoFilePath(),
                video.getTitle(),
                video.getDescription()
            );
            log.info("Uploaded to YouTube Shorts: " + youtubeId);
            logService.info("Uploaded to YouTube Shorts: " + youtubeId, video.getChannel());
        } catch (Exception e) {
            log.severe("Failed to upload to YouTube: " + e.getMessage());
            logService.error("Failed to upload to YouTube: " + e.getMessage(), video.getChannel());
        }
    }
    
    private String generateTitle(String channel) {
        return switch (channel.toLowerCase()) {
            case "finance" -> "💰 Investing Tip You NEED to Know";
            case "business" -> "💼 Side Hustle That Actually Works";
            case "tech" -> "Amazing Tech Tip You Need to Know!";
            case "facts" -> "Mind-Blowing Fact You Won't Believe";
            default -> channel + " Content";
        };
    }
}
