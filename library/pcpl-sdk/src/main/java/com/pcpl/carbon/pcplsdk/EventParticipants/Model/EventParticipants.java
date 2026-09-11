package com.pcpl.carbon.pcplsdk.EventParticipants.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "cb_carbon_event_participants")
@Data
@RequiredArgsConstructor
public class EventParticipants extends ApplicationModel {

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;
}
