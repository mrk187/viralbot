package com.viralbot.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaUsageDTO {
    private QuotaInfo gemini;
    private QuotaInfo googleTts;
    private QuotaInfo pexels;
    private QuotaInfo youtube;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotaInfo {
        private String name;
        private int used;
        private int limit;
        private double percentage;
        private String unit;
    }
}
