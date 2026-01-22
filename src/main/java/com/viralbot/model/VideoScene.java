package com.viralbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoScene {
    private String scriptSegment;
    private String videoQuery;
    private int durationSeconds;
}
