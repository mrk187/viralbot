package com.viralbot.platform;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.viralbot.config.ViralBotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class YouTubeService {
    
    private final ViralBotConfig config;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    
    public String uploadShort(String videoPath, String title, String description) {
        try {
            YouTube youtube = getYouTubeService();
            
            Video video = new Video();
            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus(config.getYoutube().getPrivacyStatus());
            video.setStatus(status);
            
            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(title + " #Shorts");
            snippet.setDescription(description + "\n\n#Shorts");
            snippet.setCategoryId(config.getYoutube().getCategoryId());
            snippet.setTags(Arrays.asList("shorts", "viral", title.toLowerCase()));
            video.setSnippet(snippet);
            
            InputStreamContent mediaContent = new InputStreamContent("video/*", new FileInputStream(videoPath));
            
            YouTube.Videos.Insert videoInsert = youtube.videos()
                .insert(Arrays.asList("snippet", "status"), video, mediaContent);
            
            Video returnedVideo = videoInsert.execute();
            String videoId = returnedVideo.getId();
            
            System.out.println("YouTube Short uploaded successfully. Video ID: {}" + videoId);
            return videoId;
            
        } catch (Exception e) {
            System.err.println("Error uploading to YouTube" + e);
            throw new RuntimeException("Failed to upload to YouTube", e);
        }
    }
    
    private YouTube getYouTubeService() throws GeneralSecurityException, IOException {
        Credential credential = GoogleCredential.fromStream(
            new FileInputStream(config.getYoutube().getCredentialsPath())
        );
        
        return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
            .setApplicationName("ViralBot")
            .build();
    }
}
