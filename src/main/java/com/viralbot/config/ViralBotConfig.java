package com.viralbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "viralbot")
public class ViralBotConfig {
    private List<String> channels;
    private Map<String, ChannelConfig> channel;
    private VideoConfig video;
    private AiConfig ai;
    private TtsConfig tts;
    private MediaConfig media;
    private FfmpegConfig ffmpeg;
    private YoutubeConfig youtube;
    private ScheduleConfig schedule;

    public List<String> getChannels() { return channels; }
    public void setChannels(List<String> channels) { this.channels = channels; }
    public Map<String, ChannelConfig> getChannel() { return channel; }
    public void setChannel(Map<String, ChannelConfig> channel) { this.channel = channel; }
    public VideoConfig getVideo() { return video; }
    public void setVideo(VideoConfig video) { this.video = video; }
    public AiConfig getAi() { return ai; }
    public void setAi(AiConfig ai) { this.ai = ai; }
    public TtsConfig getTts() { return tts; }
    public void setTts(TtsConfig tts) { this.tts = tts; }
    public MediaConfig getMedia() { return media; }
    public void setMedia(MediaConfig media) { this.media = media; }
    public FfmpegConfig getFfmpeg() { return ffmpeg; }
    public void setFfmpeg(FfmpegConfig ffmpeg) { this.ffmpeg = ffmpeg; }
    public YoutubeConfig getYoutube() { return youtube; }
    public void setYoutube(YoutubeConfig youtube) { this.youtube = youtube; }
    public ScheduleConfig getSchedule() { return schedule; }
    public void setSchedule(ScheduleConfig schedule) { this.schedule = schedule; }
    
    public static class ChannelConfig {
        private String description;
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class VideoConfig {
        private int duration;
        private String format;
        private String resolution;
        private int fps;
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
        public int getFps() { return fps; }
        public void setFps(int fps) { this.fps = fps; }
    }
    
    public static class AiConfig {
        private GeminiConfig gemini;
        public GeminiConfig getGemini() { return gemini; }
        public void setGemini(GeminiConfig gemini) { this.gemini = gemini; }
    }
    
    public static class GeminiConfig {
        private String apiKey;
        private String model;
        private int maxTokens;
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    }
    
    public static class TtsConfig {
        private GoogleTtsConfig google;
        public GoogleTtsConfig getGoogle() { return google; }
        public void setGoogle(GoogleTtsConfig google) { this.google = google; }
    }
    
    public static class GoogleTtsConfig {
        private String credentialsPath;
        private String languageCode;
        private String voiceName;
        public String getCredentialsPath() { return credentialsPath; }
        public void setCredentialsPath(String credentialsPath) { this.credentialsPath = credentialsPath; }
        public String getLanguageCode() { return languageCode; }
        public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
        public String getVoiceName() { return voiceName; }
        public void setVoiceName(String voiceName) { this.voiceName = voiceName; }
    }
    
    public static class MediaConfig {
        private PexelsConfig pexels;
        private int imagesPerVideo;
        private int imageDuration;
        public PexelsConfig getPexels() { return pexels; }
        public void setPexels(PexelsConfig pexels) { this.pexels = pexels; }
        public int getImagesPerVideo() { return imagesPerVideo; }
        public void setImagesPerVideo(int imagesPerVideo) { this.imagesPerVideo = imagesPerVideo; }
        public int getImageDuration() { return imageDuration; }
        public void setImageDuration(int imageDuration) { this.imageDuration = imageDuration; }
    }
    
    public static class PexelsConfig {
        private String apiKey;
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }
    
    public static class FfmpegConfig {
        private String path;
        private String tempDir;
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getTempDir() { return tempDir; }
        public void setTempDir(String tempDir) { this.tempDir = tempDir; }
    }
    
    public static class YoutubeConfig {
        private String clientId;
        private String clientSecret;
        private String credentialsPath;
        private String categoryId;
        private String privacyStatus;
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        public String getCredentialsPath() { return credentialsPath; }
        public void setCredentialsPath(String credentialsPath) { this.credentialsPath = credentialsPath; }
        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
        public String getPrivacyStatus() { return privacyStatus; }
        public void setPrivacyStatus(String privacyStatus) { this.privacyStatus = privacyStatus; }
    }
    
    public static class ScheduleConfig {
        private boolean enabled;
        private String cron;
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getCron() { return cron; }
        public void setCron(String cron) { this.cron = cron; }
    }
}
