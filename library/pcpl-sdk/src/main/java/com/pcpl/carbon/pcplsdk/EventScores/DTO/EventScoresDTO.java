package com.pcpl.carbon.pcplsdk.EventScores.DTO;

import com.pcpl.carbon.pcplsdk.EventScores.Model.EventScores;
import lombok.Data;

import java.util.Date;

@Data
public class EventScoresDTO {
    private Long id;
    private Long eventId;
    private Long participantId;
    private Long tenantId;
    private Double score;
    private String time;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private Integer  isDeleted;
    private Long deletedBy;
    private Date deletedTime;
    private String eventName;
    private String participantName;
    private Integer  eventScoreType;
    private String eventType;

    public EventScoresDTO() {
    }

    public EventScoresDTO(Long id, Long eventId, Long participantId, Double score,String time, Long createdBy, Date creationTime, Long lastModifiedBy, Date lastModifiedTime, int isDeleted, Long deletedBy, Date deletedTime) {
        this.id = id;
        this.eventId = eventId;
        this.participantId = participantId;
        this.score = score;
        this.time = time;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedTime = lastModifiedTime;
        this.isDeleted = isDeleted;
        this.deletedBy = deletedBy;
        this.deletedTime = deletedTime;
    }

    public EventScoresDTO(Long id, Long eventId, Long participantId, Double score,String time, Long createdBy, Date creationTime, Long lastModifiedBy, Date lastModifiedTime, Integer isDeleted, Long deletedBy, Date deletedTime, String eventName, String participantName, Integer eventScoreType,String eventType) {
        this.id = id;
        this.eventId = eventId;
        this.participantId = participantId;
        this.score = score;
        this.time = time;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedTime = lastModifiedTime;
        this.isDeleted = isDeleted;
        this.deletedBy = deletedBy;
        this.deletedTime = deletedTime;
        this.eventName = eventName;
        this.participantName = participantName;
        this.eventScoreType = eventScoreType;
        this.eventType = eventType;
    }

    public EventScoresDTO(EventScores eventScores) {
        this.id = eventScores.getId();
        this.eventId = eventScores.getEventId();
        this.participantId = eventScores.getParticipantId();
        this.score = eventScores.getScore();
        this.time = eventScores.getTime();
        this.createdBy = eventScores.getCreatedBy();
        this.creationTime = eventScores.getCreationTime();
        this.lastModifiedBy = eventScores.getLastModifiedBy();
        this.lastModifiedTime = eventScores.getLastModifiedTime();
        this.isDeleted = eventScores.getIsDeleted();
        this.deletedBy = eventScores.getDeletedBy();
        this.deletedTime = eventScores.getDeletedTime();
    }
}
