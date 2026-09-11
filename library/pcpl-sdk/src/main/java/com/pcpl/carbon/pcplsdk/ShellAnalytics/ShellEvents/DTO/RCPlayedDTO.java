package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RCPlayedDTO {
    private Long id;
    private Long tenantId;
    private String carName;
    private float rcDuration;
    private String gameId;
    private String country;
}
