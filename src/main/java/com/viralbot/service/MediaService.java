package com.viralbot.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.viralbot.config.ViralBotConfig;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaService {
    
    private final ViralBotConfig config;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    
    public String fetchBackgroundVideo(String channelName) {
        String url = String.format(
            "https://api.pexels.com/videos/search?query=%s&per_page=1&orientation=portrait",
            channelName.replace(" ", "+")
        );
        
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", config.getMedia().getPexels().getApiKey())
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                JsonArray videos = jsonResponse.getAsJsonArray("videos");
                
                if (videos.size() > 0) {
                    JsonObject video = videos.get(0).getAsJsonObject();
                    JsonArray videoFiles = video.getAsJsonArray("video_files");
                    
                    // Find HD portrait video
                    for (int i = 0; i < videoFiles.size(); i++) {
                        JsonObject file = videoFiles.get(i).getAsJsonObject();
                        if (file.get("quality").getAsString().equals("hd")) {
                            String videoUrl = file.get("link").getAsString();
                            System.out.println("Fetched background video for: " + channelName);
                            return videoUrl;
                        }
                    }
                    
                    // Fallback to first video if no HD found
                    String videoUrl = videoFiles.get(0).getAsJsonObject().get("link").getAsString();
                    System.out.println("Fetched background video (fallback) for: " + channelName);
                    return videoUrl;
                }
            }
            System.err.println("No videos found for: " + channelName);
        } catch (IOException e) {
            System.err.println("Error fetching video from Pexels: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<String> fetchImages(String query) {
        List<String> imageUrls = new ArrayList<>();
        
        if (config.getMedia() == null || config.getMedia().getPexels() == null) {
            System.out.println("Media configuration not found, returning empty list");
            return imageUrls;
        }
        
        String url = String.format(
            "https://api.pexels.com/v1/search?query=%s&per_page=%d&orientation=portrait",
            query.replace(" ", "+"),
            config.getMedia().getImagesPerVideo()
        );
        
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", config.getMedia().getPexels().getApiKey())
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                JsonArray photos = jsonResponse.getAsJsonArray("photos");
                
                for (int i = 0; i < photos.size(); i++) {
                    JsonObject photo = photos.get(i).getAsJsonObject();
                    String imageUrl = photo.getAsJsonObject("src").get("large2x").getAsString();
                    imageUrls.add(imageUrl);
                }
                
                System.out.println("Fetched " + imageUrls.size() + " images for query: " + query);
            }
        } catch (IOException e) {
            System.err.println("Error fetching images from Pexels: " + e.getMessage());
        }
        
        return imageUrls;
    }
}
