package com.viralbot.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatusDTO {
    private int videosGeneratedToday;
    private int videosGeneratedThisWeek;
    private int videosGeneratedThisMonth;
    private int successfulUploads;
    private int failedUploads;
    private double successRate;
    private String currentTask;
    private String nextScheduledRun;
    private boolean schedulerEnabled;
}
