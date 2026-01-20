package com.viralbot.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketEventPublisher {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void publishLog(String level, String message) {
        LogMessage logMessage = new LogMessage(level, message, System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/logs", logMessage);
    }
    
    public void publishStatus(String status) {
        messagingTemplate.convertAndSend("/topic/status", status);
    }
    
    public void publishProgress(String task, int percentage) {
        ProgressMessage progress = new ProgressMessage(task, percentage);
        messagingTemplate.convertAndSend("/topic/progress", progress);
    }
    
    record LogMessage(String level, String message, long timestamp) {}
    record ProgressMessage(String task, int percentage) {}
}
