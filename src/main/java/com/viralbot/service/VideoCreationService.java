package com.viralbot.service;

import com.viralbot.config.ViralBotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoCreationService {
    
    private final ViralBotConfig config;
    
    public String createVideo(String audioPath, String backgroundVideoUrl, String outputFileName) {
        try {
            Path tempDir = Paths.get(config.getFfmpeg().getTempDir());
            Files.createDirectories(tempDir);
            
            // Download background video
            String videoPath = tempDir.resolve("background.mp4").toString();
            ProcessBuilder downloadPb = new ProcessBuilder("curl", "-o", videoPath, backgroundVideoUrl);
            downloadPb.start().waitFor();
            System.out.println("Downloaded background video");
            
            String outputPath = tempDir.resolve(outputFileName + ".mp4").toString();
            
            List<String> command = new ArrayList<>();
            command.add(config.getFfmpeg().getPath());
            command.add("-y");
            command.add("-stream_loop");
            command.add("-1"); // Loop video indefinitely
            command.add("-i");
            command.add(videoPath);
            command.add("-i");
            command.add(audioPath);
            command.add("-filter_complex");
            command.add("[0:v]scale=1080:1920:force_original_aspect_ratio=increase,crop=1080:1920,setsar=1[v]");
            command.add("-map");
            command.add("[v]");
            command.add("-map");
            command.add("1:a");
            command.add("-c:v");
            command.add("libx264");
            command.add("-c:a");
            command.add("aac");
            command.add("-shortest"); // Stop when audio ends
            command.add("-pix_fmt");
            command.add("yuv420p");
            command.add(outputPath);
            
            System.out.println("FFmpeg command: " + String.join(" ", command));
            
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // Log output in real-time
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFmpeg: " + line);
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Video created successfully: " + outputPath);
                return outputPath;
            } else {
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
            }
            
        } catch (Exception e) {
            System.err.println("Error creating video: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create video", e);
        }
    }
    


}
