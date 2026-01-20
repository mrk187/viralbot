package com.viralbot.service;

import com.viralbot.config.ViralBotConfig;
import com.viralbot.model.Video;
import com.viralbot.platform.TikTokService;
import com.viralbot.platform.YouTubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ViralBotOrchestrator {
    
    private final ViralBotConfig config;
    private final ContentGeneratorService contentGenerator;
    private final TextToSpeechService ttsService;
    private final MediaService mediaService;
    private final VideoCreationService videoCreationService;
    private final YouTubeService youtubeService;
    private final TikTokService tiktokService;
    
    @Scheduled(cron = "${viralbot.schedule.cron}")
    public void executeScheduledTask() {
        if (config.getSchedule().isEnabled()) {
            System.out.println("Starting scheduled video generation and upload");
            processAllChannels();
        }
    }
    
    public void processAllChannels() {
        for (String channel : config.getChannels()) {
            try {
                processChannel(channel);
            } catch (Exception e) {
                System.err.println("Error processing channel: " + channel + " - " + e.getMessage());
            }
        }
    }
    
    public void processChannel(String channel) {
        System.out.println("Processing channel: " + channel);
        
        String channelDescription = config.getChannel().get(channel).getDescription();
        String script = contentGenerator.generateScript(channel, channelDescription);
        System.out.println("Generated script for " + channel + ": " + script.length() + " chars");
        
        String audioFileName = String.format("%s_%s", channel, UUID.randomUUID());
        String audioPath = ttsService.generateAudio(script, audioFileName);
        
        String backgroundVideoUrl = mediaService.fetchBackgroundVideo(channel);
        if (backgroundVideoUrl == null) {
            throw new RuntimeException("Failed to fetch background video for channel: " + channel);
        }
        
        String videoFileName = String.format("%s_%s", channel, UUID.randomUUID());
        String videoPath = videoCreationService.createVideo(audioPath, backgroundVideoUrl, videoFileName);
        
        Video video = Video.builder()
            .channel(channel)
            .title(generateTitle(channel))
            .description(script.substring(0, Math.min(script.length(), 200)))
            .scriptContent(script)
            .audioFilePath(audioPath)
            .videoFilePath(videoPath)
            .durationSeconds(config.getVideo().getDuration())
            .build();
        
        uploadToPlatforms(video);
        
        System.out.println("Successfully processed channel: " + channel);
    }
    
    private void uploadToPlatforms(Video video) {
        try {
            String youtubeId = youtubeService.uploadShort(
                video.getVideoFilePath(),
                video.getTitle(),
                video.getDescription()
            );
            System.out.println("Uploaded to YouTube Shorts: " + youtubeId);
        } catch (Exception e) {
            System.err.println("Failed to upload to YouTube: " + e.getMessage());
        }
        
        try {
            String tiktokId = tiktokService.uploadVideo(
                video.getVideoFilePath(),
                video.getTitle(),
                video.getDescription()
            );
            System.out.println("Uploaded to TikTok: " + tiktokId);
        } catch (Exception e) {
            System.err.println("Failed to upload to TikTok: " + e.getMessage());
        }
    }
    
    private String generateTitle(String channel) {
        return switch (channel.toLowerCase()) {
            case "boxing" -> "Boxing Legend Motivation 🥊";
            case "tech" -> "Amazing Tech Tip You Need to Know!";
            case "motivation" -> "Daily Motivation to Transform Your Life";
            case "facts" -> "Mind-Blowing Fact You Won't Believe";
            default -> channel + " Content";
        };
    }
}
