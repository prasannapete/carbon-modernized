package com.pcpl.carbon.kitkat_service.EventScores.Repository;

import com.pcpl.carbon.pcplsdk.EventScores.DTO.EventScoresDTO;
import com.pcpl.carbon.pcplsdk.EventScores.Model.EventScores;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EventScoresRepository extends PCPLCRUDRepository<EventScores> {

    List<EventScores> findAllByIsDeleted(int isDeleted);

    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.EventScores.DTO.EventScoresDTO(" +
                    "eventScore.id, " +
                    "eventScore.eventId, " +
                    "eventScore.participantId, " +
                    "eventScore.score, " +
                    "eventScore.time, " +
                    "eventScore.createdBy, " +
                    "eventScore.creationTime, " +
                    "eventScore.lastModifiedBy, " +
                    "eventScore.lastModifiedTime, " +
                    "eventScore.isDeleted, " +
                    "eventScore.deletedBy, " +
                    "eventScore.deletedTime, " +
                    "events.eventName, " +
                    "eventParticipants.name," +
                    "events.scoreType,"+
                    "events.eventType"+
                    " )" +
                    " FROM EventScores as eventScore" +
                    " LEFT JOIN Events events on eventScore.eventId=events.id AND eventScore.isDeleted = 0" +
                    " LEFT JOIN EventParticipants eventParticipants on eventScore.participantId=eventParticipants.id and eventParticipants.isDeleted = 0" +
                    " WHERE eventScore.isDeleted=0 and eventScore.eventId =:eventId and eventScore.participantId =:participantId"

    )
    Page<EventScoresDTO> findAllByIsDeletedParticipantIdAndEventId(@Param("eventId") Long eventId, @Param("participantId") Long participantId, Pageable pageable);

    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.EventScores.DTO.EventScoresDTO(" +
                    "eventScore.id, " +
                    "eventScore.eventId, " +
                    "eventScore.participantId, " +
                    "eventScore.score, " +
                    "eventScore.time, " +
                    "eventScore.createdBy, " +
                    "eventScore.creationTime, " +
                    "eventScore.lastModifiedBy, " +
                    "eventScore.lastModifiedTime, " +
                    "eventScore.isDeleted, " +
                    "eventScore.deletedBy, " +
                    "eventScore.deletedTime, " +
                    "events.eventName, " +
                    "eventParticipants.name," +
                    "events.scoreType,"+
                    "events.eventType"+
                    " )" +
                    " FROM EventScores as eventScore" +
                    " LEFT JOIN Events events on eventScore.eventId=events.id AND eventScore.isDeleted = 0" +
                    " LEFT JOIN EventParticipants eventParticipants on eventScore.participantId=eventParticipants.id and eventParticipants.isDeleted = 0" +
                    " WHERE eventScore.isDeleted=0 and eventScore.eventId =:eventId And (eventScore.score is not null OR eventScore.time is not null)"

    )
    Page<EventScoresDTO> findAllByIsDeletedEventId(@Param("eventId") Long eventId, Pageable pageable);
}
