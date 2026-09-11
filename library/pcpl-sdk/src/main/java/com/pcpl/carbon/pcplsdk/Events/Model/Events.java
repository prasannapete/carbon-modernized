package com.pcpl.carbon.pcplsdk.Events.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "cb_carbon_events")
@Data
@RequiredArgsConstructor
public class Events extends ApplicationModel {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "score_type",columnDefinition = "INT DEFAULT 0")
    private int scoreType;

    @Column(name = "is_live",columnDefinition = "INT DEFAULT 0")
    private int isLive;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "primary_color")
    private String primaryColor;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "console_id")
    private int consoleId;
}
