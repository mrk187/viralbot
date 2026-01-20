package com.viralbot.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.viralbot.config.ViralBotConfig;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ContentGeneratorService {
    
    private final ViralBotConfig config;
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build();
    private final Gson gson = new Gson();
    
    public String generateScript(String channel, String channelDescription) {
        String prompt = String.format(
            "Create a 60-80 second engaging video script for a %s channel. " +
            "Channel focus: %s. " +
            "Requirements: Hook in first 3 seconds, clear narrative, call-to-action at end. " +
            "IMPORTANT: Script must be exactly 60-80 seconds when spoken naturally (about 125-140 words). " +
            "Format: Just the script text, no labels or formatting.",
            channel, channelDescription
        );
        return callGeminiApi(prompt);
    }
    
    private String callGeminiApi(String prompt) {
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
            System.err.println("Gemini API error: " + response.code() + " - " + errorBody);
            System.err.println("Request URL: " + url);
            return "Failed to generate content";
        } catch (IOException e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            return "Failed to generate content";
        }
    }
}
