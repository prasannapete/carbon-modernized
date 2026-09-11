package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeConfigurationTranslationDTO {
    private Long id;
    private Long codeConfigurationId;
    private Long tenantId;
    private String langCode;
    private String eventTitle;
    private String eventReward;
    private String eventDescription;
    private String eventHowToClaim;
    private int EventType;
    private int isActive;

}
