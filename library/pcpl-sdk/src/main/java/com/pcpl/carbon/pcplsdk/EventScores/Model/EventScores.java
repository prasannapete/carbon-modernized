package com.pcpl.carbon.pcplsdk.EventScores.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "cb_carbon_event_scores")
@Data
@RequiredArgsConstructor
public class EventScores extends ApplicationModel {

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "participant_id")
    private Long participantId;

    @Column(name = "score")
    private Double score;

    @Column(name = "time")
    private String time;
}
