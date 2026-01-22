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
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class VideoCreationService {
    
    private static final Logger log = Logger.getLogger(VideoCreationService.class.getName());
    
    private final ViralBotConfig config;
    
    public String createVideoFromBackgroundVideo(String audioPath, String videoUrl, String outputFileName, int targetDuration) {
        Path tempDir = Paths.get(config.getFfmpeg().getTempDir());
        
        try {
            Files.createDirectories(tempDir);
            
            // Download video
            String videoPath = tempDir.resolve("background.mp4").toString();
            ProcessBuilder pb = new ProcessBuilder("curl", "-L", "-m", "60", "-o", videoPath, videoUrl);
            if (!pb.start().waitFor(65, java.util.concurrent.TimeUnit.SECONDS)) {
                throw new RuntimeException("Video download timeout");
            }
            log.info("Downloaded background video");
            
            String outputPath = tempDir.resolve(outputFileName + ".mp4").toString();
            String[] res = config.getVideo().getResolution().split("x");
            
            // FFmpeg: trim video, add audio, crop to portrait
            List<String> cmd = new ArrayList<>();
            cmd.add(config.getFfmpeg().getPath());
            cmd.add("-y");
            cmd.add("-i"); cmd.add(videoPath);
            cmd.add("-i"); cmd.add(audioPath);
            cmd.add("-t"); cmd.add(String.valueOf(targetDuration));
            cmd.add("-vf"); cmd.add(String.format("scale=%s:%s:force_original_aspect_ratio=increase,crop=%s:%s,fps=%d", 
                res[0], res[1], res[0], res[1], config.getVideo().getFps()));
            cmd.add("-c:v"); cmd.add("libx264");
            cmd.add("-c:a"); cmd.add("aac");
            cmd.add("-shortest");
            cmd.add("-pix_fmt"); cmd.add("yuv420p");
            cmd.add(outputPath);
            
            log.info("FFmpeg: " + String.join(" ", cmd));
            
            Process proc = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                r.lines().forEach(log::info);
            }
            
            if (proc.waitFor() != 0) throw new RuntimeException("FFmpeg failed");
            
            Files.deleteIfExists(Path.of(videoPath));
            
            log.info("Video created: " + outputPath);
            return outputPath;
            
        } catch (Exception e) {
            log.severe("Error: " + e.getMessage());
            throw new RuntimeException("Failed to create video", e);
        }
    }
    


}
