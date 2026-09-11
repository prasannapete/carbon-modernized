package com.pcpl.carbon.pcplsdk.EventParticipants.Model;

import com.pcpl.carbon.pcplsdk.Common.Model.ApplicationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "cb_carbon_event_participants")
@Data
@RequiredArgsConstructor
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
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
