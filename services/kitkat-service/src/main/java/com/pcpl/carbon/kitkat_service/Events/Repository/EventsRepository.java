package com.pcpl.carbon.kitkat_service.Events.Repository;

import com.pcpl.carbon.pcplsdk.Events.DTO.EventsDTO;
import com.pcpl.carbon.pcplsdk.Events.Model.Events;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface EventsRepository extends PCPLCRUDRepository<Events> {

    List<Events> findAllByIsDeleted(int isDeleted, Sort sort);
    List<Events> findAllByIsDeleted(int isDeleted);
    Page<Events> findAllByIsDeleted(int isDeleted, Pageable pageable);
    Page<Events> findAll(Specification<Events> textInAllColumns, Pageable pageable);
    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.Events.DTO.EventsDTO(" +
                    "evets.id, " +
                    "evets.eventName, " +
                    "evets.description, " +
                    "evets.startDate, " +
                    "evets.endDate, " +
                    "evets.scoreType, " +
                    "evets.isLive, " +
                    "evets.logoPath, " +
                    "evets.primaryColor, " +
                    "evets.eventType,"+
                    "evets.consoleId,"+
                    "evets.createdBy, " +
                    "evets.creationTime, " +
                    "evets.lastModifiedBy, " +
                    "evets.lastModifiedTime, " +
                    "evets.isDeleted, " +
                    "evets.deletedBy, " +
                    "evets.deletedTime " +
                    " )" +
                    " FROM Events as evets" +
                    " WHERE evets.isDeleted=0 and evets.isLive = 1 and evets.startDate<= :currentDate and evets.endDate>= :currentDate"

    )
    List<EventsDTO> findAllByIsDeletedAndIsLiveAndValidDate(@Param("currentDate") Date currentDate, Sort sort);

    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.Events.DTO.EventsDTO(" +
                    "events.id, " +
                    "events.eventName, " +
                    "events.description, " +
                    "events.startDate, " +
                    "events.endDate, " +
                    "events.scoreType, " +
                    "events.isLive, " +
                    "events.logoPath, " +
                    "events.primaryColor, " +
                    "events.eventType,"+
                    "events.consoleId,"+
                    "events.createdBy, " +
                    "events.creationTime, " +
                    "events.lastModifiedBy, " +
                    "events.lastModifiedTime, " +
                    "events.isDeleted, " +
                    "events.deletedBy, " +
                    "events.deletedTime " +
                    " )" +
                    " FROM Events as events" +
                    " WHERE events.isDeleted=0 and events.isLive = 1 and events.endDate >= CURRENT_DATE"
    )
    List<EventsDTO> findAllByIsDeletedEvents();
}
