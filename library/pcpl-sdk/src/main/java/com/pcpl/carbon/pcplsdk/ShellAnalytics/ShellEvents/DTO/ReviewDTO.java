package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.Data;

import java.util.Map;

@Data
public class ReviewDTO {
    private String id;
    private Map<String, String> attributes;
}
