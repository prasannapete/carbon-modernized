package com.pcpl.carbon.pcplsdk.EventParticipants.DTO;

import com.pcpl.carbon.pcplsdk.EventParticipants.Model.EventParticipants;
import lombok.Data;

import java.util.Date;

@Data
public class EventParticipantsDTO {
    private Long id;
    private Long eventId;
    private Long tenantId;
    private String name;
    private int age;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private int isDeleted;
    private Long deletedBy;
    private Date deletedTime;
    private String eventName;
    private Double eventScores;
    private String eventTime;
    private Long eventScoreId;
    private int eventScoreType;

    public EventParticipantsDTO() {
    }

    public EventParticipantsDTO(Long id, Long eventId, String name, int age, Long createdBy, Date creationTime, Long lastModifiedBy, Date lastModifiedTime, int isDeleted, Long deletedBy, Date deletedTime) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.age = age;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedTime = lastModifiedTime;
        this.isDeleted = isDeleted;
        this.deletedBy = deletedBy;
        this.deletedTime = deletedTime;
    }

    public EventParticipantsDTO(Long id, Long eventId, String name, int age, Long createdBy, Date creationTime, Long lastModifiedBy, Date lastModifiedTime, int isDeleted, Long deletedBy, Date deletedTime, String eventName, Double eventScores,String eventTime, Long eventScoreId,int eventScoreType) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.age = age;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedTime = lastModifiedTime;
        this.isDeleted = isDeleted;
        this.deletedBy = deletedBy;
        this.deletedTime = deletedTime;
        this.eventName = eventName;
        this.eventScores = eventScores;
        this.eventTime = eventTime;
        this.eventScoreId = eventScoreId;
        this.eventScoreType = eventScoreType;
    }

    public EventParticipantsDTO(EventParticipants eventParticipants) {
        this.id = eventParticipants.getId();
        this.eventId = eventParticipants.getEventId();
        this.name = eventParticipants.getName();
        this.age = eventParticipants.getAge();
        this.createdBy = eventParticipants.getCreatedBy();
        this.creationTime = eventParticipants.getCreationTime();
        this.lastModifiedBy = eventParticipants.getLastModifiedBy();
        this.lastModifiedTime = eventParticipants.getLastModifiedTime();
        this.isDeleted = eventParticipants.getIsDeleted();
        this.deletedBy = eventParticipants.getDeletedBy();
        this.deletedTime = eventParticipants.getDeletedTime();
    }
}
