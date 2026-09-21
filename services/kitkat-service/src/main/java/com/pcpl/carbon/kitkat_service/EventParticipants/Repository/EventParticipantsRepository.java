package com.pcpl.carbon.kitkat_service.EventParticipants.Repository;

import com.pcpl.carbon.pcplsdk.EventParticipants.DTO.EventParticipantsDTO;
import com.pcpl.carbon.pcplsdk.EventParticipants.Model.EventParticipants;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface EventParticipantsRepository extends PCPLCRUDRepository<EventParticipants> {

    List<EventParticipants> findAllByIsDeleted(int isDeleted, Sort sort);
    List<EventParticipants> findAllByIsDeleted(int isDeleted);
    Page<EventParticipants> findAllByIsDeleted(int isDeleted, Pageable pageable);
    Page<EventParticipants> findAll(Specification<EventParticipants> textInAllColumns, Pageable pageable);

    List<EventParticipants> findAllByIsDeletedAndEventIdAndName(int isDeleted,Long eventId, String name);

    List<EventParticipants> findAllByIsDeletedAndEventIdAndNameAndIdIsNot(int isDeleted,Long eventId, String name,Long id);

    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.EventParticipants.DTO.EventParticipantsDTO(" +
                    "eventParticipants.id, " +
                    "eventParticipants.eventId, " +
                    "eventParticipants.name, " +
                    "eventParticipants.age, " +
                    "eventParticipants.createdBy, " +
                    "eventParticipants.creationTime, " +
                    "eventParticipants.lastModifiedBy, " +
                    "eventParticipants.lastModifiedTime, " +
                    "eventParticipants.isDeleted, " +
                    "eventParticipants.deletedBy, " +
                    "eventParticipants.deletedTime, " +
                    "events.eventName, " +
                    "eventScores.score," +
                    "eventScores.time," +
                    "eventScores.id,"+
                    "events.scoreType"+
                    " )" +
                    " FROM EventParticipants as eventParticipants" +
                    " LEFT JOIN Events events on eventParticipants.eventId=events.id and events.isDeleted =0" +
                    " LEFT JOIN EventScores eventScores on eventScores.participantId=eventParticipants.id and eventScores.isDeleted =0" +
                    " WHERE eventParticipants.isDeleted=0 and eventParticipants.eventId =:eventId"

    )
    Page<EventParticipantsDTO> findAllByEventId(@Param("eventId") Long eventId, Pageable pageable);

}
