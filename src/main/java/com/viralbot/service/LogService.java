package com.viralbot.service;

import com.viralbot.model.LogEntry;
import com.viralbot.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    
    private final LogRepository logRepository;
    
    public void log(String level, String message, String channel) {
        LogEntry entry = LogEntry.builder()
            .level(level)
            .message(message)
            .channel(channel)
            .build();
        logRepository.save(entry);
    }
    
    public void info(String message, String channel) {
        log("INFO", message, channel);
    }
    
    public void warn(String message, String channel) {
        log("WARN", message, channel);
    }
    
    public void error(String message, String channel) {
        log("ERROR", message, channel);
    }
    
    public List<LogEntry> getRecentLogs() {
        return logRepository.findTop100ByOrderByCreatedAtDesc();
    }
}
