package com.viralbot.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import com.viralbot.config.ViralBotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class TextToSpeechService {
    
    private final ViralBotConfig config;
    
    public String generateAudio(String text, String outputFileName) {
        try {
            // Load credentials from classpath
            InputStream credentialsStream = new ClassPathResource(config.getTts().getGoogle().getCredentialsPath()).getInputStream();
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            
            TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();
            
            TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings);
            
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode(config.getTts().getGoogle().getLanguageCode())
                .setName(config.getTts().getGoogle().getVoiceName())
                .build();
            
            AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();
            
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();
            
            Path tempDir = Paths.get(config.getFfmpeg().getTempDir());
            Files.createDirectories(tempDir);
            
            String outputPath = tempDir.resolve(outputFileName + ".mp3").toString();
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                out.write(audioContents.toByteArray());
            }
            
            textToSpeechClient.close();
            
            System.out.println("Audio generated: " + outputPath);
            return outputPath;
            
        } catch (Exception e) {
            System.err.println("Error generating audio: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate audio", e);
        }
    }
}
