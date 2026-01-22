package com.viralbot.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.viralbot.config.ViralBotConfig;
import com.viralbot.model.VideoScene;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ContentGeneratorService {
    
    private static final Logger log = Logger.getLogger(ContentGeneratorService.class.getName());
    
    private final ViralBotConfig config;
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build();
    private final Gson gson = new Gson();
    
    public String generateScript(String channel, String channelDescription, int durationSeconds) {
        int wordCount = durationSeconds * 3; // 3 words per second for natural speech
        
        String prompt = String.format(
            "Write a %d-second video script (%d words) for %s: %s. " +
            "Hook in first 3 seconds. End with soft call-to-action. " +
            "Return ONLY the script text, no formatting.",
            durationSeconds, wordCount, channel, channelDescription
        );
        
        return callGeminiApi(prompt);
    }
    
    private String callGeminiApi(String prompt) {
        int maxRetries = 3;
        int retryDelay = 2000; // Start with 2 seconds
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            String url = String.format(
                "https://generativelanguage.googleapis.com/v1/models/%s:generateContent?key=%s",
                config.getAi().getGemini().getModel(),
                config.getAi().getGemini().getApiKey()
            );
            
            JsonObject parts = new JsonObject();
            parts.addProperty("text", prompt);
            
            JsonObject content = new JsonObject();
            content.add("parts", gson.toJsonTree(new JsonObject[]{gson.fromJson(parts, JsonObject.class)}));
            
            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", gson.toJsonTree(new JsonObject[]{gson.fromJson(content, JsonObject.class)}));
            
            Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json")))
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
                    return jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject()
                        .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject()
                        .get("text").getAsString();
                }
                
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                
                // Retry on 503 (overloaded) or 429 (rate limit)
                if ((response.code() == 503 || response.code() == 429) && attempt < maxRetries) {
                    log.warning("Gemini API " + response.code() + " - Retry " + attempt + "/" + maxRetries + " in " + retryDelay + "ms");
                    Thread.sleep(retryDelay);
                    retryDelay *= 2; // Exponential backoff
                    continue;
                }
                
                log.severe("Gemini API error: " + response.code() + " - " + errorBody);
                return "Failed to generate content";
            } catch (IOException e) {
                log.severe("Error calling Gemini API: " + e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                        retryDelay *= 2;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                return "Failed to generate content";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Failed to generate content";
            }
        }
        
        return "Failed to generate content";
    }
}
