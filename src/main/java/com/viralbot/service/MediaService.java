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
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class MediaService {
    
    private static final Logger log = Logger.getLogger(MediaService.class.getName());
    
    private final ViralBotConfig config;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    
    public String fetchBackgroundVideo(String channelName) {
        int count = config.getMedia().getImagesPerVideo();
        int randomPage = 1 + (int)(Math.random() * 10); // Random page 1-10
        String url = String.format(
            "https://api.pexels.com/videos/search?query=%s&per_page=%d&page=%d&orientation=portrait",
            channelName.replace(" ", "+"), count, randomPage
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
                    // Pick random video from results
                    int randomIndex = (int)(Math.random() * videos.size());
                    JsonObject video = videos.get(randomIndex).getAsJsonObject();
                    JsonArray videoFiles = video.getAsJsonArray("video_files");
                    
                    // Find HD portrait video
                    for (int i = 0; i < videoFiles.size(); i++) {
                        JsonObject file = videoFiles.get(i).getAsJsonObject();
                        if (file.get("quality").getAsString().equals("hd")) {
                            String videoUrl = file.get("link").getAsString();
                            log.info("Fetched background video (page " + randomPage + ", index " + randomIndex + ") for: " + channelName);
                            return videoUrl;
                        }
                    }
                    
                    // Fallback to first video if no HD found
                    String videoUrl = videoFiles.get(0).getAsJsonObject().get("link").getAsString();
                    log.info("Fetched background video (fallback) for: " + channelName);
                    return videoUrl;
                }
            }
            log.warning("No videos found for: " + channelName);
        } catch (IOException e) {
            log.severe("Error fetching video from Pexels: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<String> fetchBackgroundVideos(String channelName) {
        List<String> videoUrls = new ArrayList<>();
        int count = config.getMedia().getImagesPerVideo();
        
        String url = String.format(
            "https://api.pexels.com/videos/search?query=%s&per_page=%d&orientation=portrait",
            channelName.replace(" ", "+"), count
        );
        
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", config.getMedia().getPexels().getApiKey())
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                JsonArray videos = jsonResponse.getAsJsonArray("videos");
                
                for (int i = 0; i < Math.min(videos.size(), count); i++) {
                    JsonObject video = videos.get(i).getAsJsonObject();
                    JsonArray videoFiles = video.getAsJsonArray("video_files");
                    
                    // Find HD portrait video
                    boolean foundHd = false;
                    for (int j = 0; j < videoFiles.size(); j++) {
                        JsonObject file = videoFiles.get(j).getAsJsonObject();
                        if (file.has("quality") && !file.get("quality").isJsonNull() && 
                            file.get("quality").getAsString().equals("hd")) {
                            videoUrls.add(file.get("link").getAsString());
                            foundHd = true;
                            break;
                        }
                    }
                    
                    // Fallback to first video if no HD found
                    if (!foundHd && videoFiles.size() > 0) {
                        videoUrls.add(videoFiles.get(0).getAsJsonObject().get("link").getAsString());
                    }
                }
                
                log.info("Fetched " + videoUrls.size() + " background videos for: " + channelName);
            }
        } catch (IOException e) {
            log.severe("Error fetching videos from Pexels: " + e.getMessage());
        }
        
        return videoUrls;
    }
    
    public List<String> fetchImagesForChannel(String channelName, int count) {
        List<String> imageUrls = new ArrayList<>();
        
        String url = String.format(
            "https://api.pexels.com/v1/search?query=%s&per_page=%d&orientation=portrait",
            channelName.replace(" ", "+"), count
        );
        
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", config.getMedia().getPexels().getApiKey())
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                JsonArray photos = jsonResponse.getAsJsonArray("photos");
                
                for (int i = 0; i < Math.min(photos.size(), count); i++) {
                    JsonObject photo = photos.get(i).getAsJsonObject();
                    String imageUrl = photo.getAsJsonObject("src").get("large2x").getAsString();
                    imageUrls.add(imageUrl);
                }
            }
        } catch (IOException e) {
            log.severe("Error fetching images from Pexels: " + e.getMessage());
        }
        
        log.info("Fetched " + imageUrls.size() + " images for channel: " + channelName);
        return imageUrls;
    }
    
    public List<String> fetchImages(String query) {
        List<String> imageUrls = new ArrayList<>();
        
        if (config.getMedia() == null || config.getMedia().getPexels() == null) {
            log.warning("Media configuration not found, returning empty list");
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
                
                log.info("Fetched " + imageUrls.size() + " images for query: " + query);
            }
        } catch (IOException e) {
            log.severe("Error fetching images from Pexels: " + e.getMessage());
        }
        
        return imageUrls;
    }
}
