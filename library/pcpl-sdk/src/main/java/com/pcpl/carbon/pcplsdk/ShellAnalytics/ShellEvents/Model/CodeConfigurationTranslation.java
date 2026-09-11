package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "srl_code_configuration_translation")
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeConfigurationTranslation extends ApplicationModel {
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "code_configuration_id")
    private Long codeConfigurationId;

    @Column(name = "lang_code")
    private String langCode;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "event_reward")
    private String eventReward;

    @Column(name = "event_description")
    private String eventDescription;

    @Column(name = "event_how_to_claim")
    private String eventHowToClaim;
}
