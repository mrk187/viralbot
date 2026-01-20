package com.viralbot.platform;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.viralbot.config.ViralBotConfig;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TikTokService {
    
    private final ViralBotConfig config;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    
    public String uploadVideo(String videoPath, String title, String description) {
        try {
            String uploadUrl = initializeUpload();
            uploadVideoFile(uploadUrl, videoPath);
            String videoId = publishVideo(title, description);
            
            System.out.println("TikTok video uploaded successfully. Video ID: {}" + videoId);
            return videoId;
            
        } catch (Exception e) {
            System.err.println("Error uploading to TikTok" + e);
            throw new RuntimeException("Failed to upload to TikTok", e);
        }
    }
    
    private String initializeUpload() throws IOException {
        String url = "https://open-api.tiktok.com/share/video/upload/";
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("access_token", config.getTiktok().getAccessToken());
        
        Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json")))
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                return jsonResponse.getAsJsonObject("data").get("upload_url").getAsString();
            }
            throw new IOException("Failed to initialize upload: " + response.code());
        }
    }
    
    private void uploadVideoFile(String uploadUrl, String videoPath) throws IOException {
        File videoFile = new File(videoPath);
        
        RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("video", videoFile.getName(),
                RequestBody.create(videoFile, MediaType.parse("video/mp4")))
            .build();
        
        Request request = new Request.Builder()
            .url(uploadUrl)
            .post(requestBody)
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to upload video file: " + response.code());
            }
        }
    }
    
    private String publishVideo(String title, String description) throws IOException {
        String url = "https://open-api.tiktok.com/share/video/create/";
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("access_token", config.getTiktok().getAccessToken());
        
        JsonObject videoInfo = new JsonObject();
        videoInfo.addProperty("title", title);
        videoInfo.addProperty("description", description);
        videoInfo.addProperty("privacy_level", "PUBLIC_TO_EVERYONE");
        requestBody.add("video_info", videoInfo);
        
        Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json")))
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                return jsonResponse.getAsJsonObject("data").get("share_id").getAsString();
            }
            throw new IOException("Failed to publish video: " + response.code());
        }
    }
}
