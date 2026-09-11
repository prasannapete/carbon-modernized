package com.pcpl.carbon.pcplsdk.Events.DTO;

import com.pcpl.carbon.pcplsdk.EventScores.DTO.EventScoresDTO;
import com.pcpl.carbon.pcplsdk.Events.Model.Events;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EventsDTO {
    private Long id;
    private Long tenantId;
    private String eventName;
    private String description;
    private Date startDate;
    private Date endDate;
    private int scoreType;
    private int isLive;
    private String logoPath;
    private String primaryColor;
    private String eventType;
    private List<EventScoresDTO> eventScoresDTOList;
    private int consoleId;
    private Long createdBy;
    private Date creationTime;
    private Long lastModifiedBy;
    private Date lastModifiedTime;
    private int isDeleted;
    private Long deletedBy;
    private Date deletedTime;


    public EventsDTO() {
    }

    public EventsDTO(Long id, String eventName, String description, Date startDate, Date endDate, int scoreType, int isLive, String logoPath, String primaryColor,String eventType,int consoleId, Long createdBy, Date creationTime, Long lastModifiedBy, Date lastModifiedTime, int isDeleted, Long deletedBy, Date deletedTime) {
        this.id = id;
        this.eventName = eventName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.scoreType = scoreType;
        this.isLive = isLive;
        this.logoPath = logoPath;
        this.primaryColor = primaryColor;
        this.eventType = eventType;
        this.consoleId = consoleId;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedTime = lastModifiedTime;
        this.isDeleted = isDeleted;
        this.deletedBy = deletedBy;
        this.deletedTime = deletedTime;

    }

    public EventsDTO(Events events) {
        this.id = events.getId();
        this.eventName = events.getEventName();
        this.description = events.getDescription();
        this.startDate = events.getStartDate();
        this.endDate = events.getEndDate();
        this.scoreType = events.getScoreType();
        this.isLive = events.getIsLive();
        this.logoPath = events.getLogoPath();
        this.primaryColor = events.getPrimaryColor();
        this.eventType = events.getEventType();
        this.consoleId = events.getConsoleId();
        this.createdBy = events.getCreatedBy();
        this.creationTime = events.getCreationTime();
        this.lastModifiedBy = events.getLastModifiedBy();
        this.lastModifiedTime = events.getLastModifiedTime();
        this.isDeleted = events.getIsDeleted();
        this.deletedBy = events.getDeletedBy();
        this.deletedTime = events.getDeletedTime();

    }
}
