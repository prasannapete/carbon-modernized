package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CodeConfigurationTranslation.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CodeConfigurationTranslationDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CodeConfigurationTranslation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CodeConfigurationTranslationRepository extends PCPLCRUDRepository<CodeConfigurationTranslation> {

     @Query("SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CodeConfigurationTranslationDTO(" +
             "codeConfigurationTranslation.id," +
             " codeConfigurationTranslation.codeConfigurationId, " +
             " codeConfigurationTranslation.tenantId, " +
             "codeConfigurationTranslation.langCode," +
             " codeConfigurationTranslation.eventTitle," +
             " codeConfigurationTranslation.eventReward," +
             " codeConfigurationTranslation.eventDescription," +
             " codeConfigurationTranslation.eventHowToClaim," +
             " offerCodeConfiguration.eventType," +
             " offerCodeConfiguration.isActive" +
             ") " +
             "FROM CodeConfigurationTranslation codeConfigurationTranslation " +
             " LEFT JOIN OfferCodeConfiguration offerCodeConfiguration  on offerCodeConfiguration.id=codeConfigurationTranslation.codeConfigurationId and offerCodeConfiguration.isDeleted=0" +
             " where codeConfigurationTranslation.isDeleted = 0 and offerCodeConfiguration.countryCode = :countryCode" +
             " group by codeConfigurationTranslation.langCode,codeConfigurationTranslation.eventTitle,codeConfigurationTranslation.eventReward,codeConfigurationTranslation.eventDescription,codeConfigurationTranslation.eventHowToClaim,codeConfigurationTranslation.codeConfigurationId,codeConfigurationTranslation.id,offerCodeConfiguration.isActive," +
             " offerCodeConfiguration.eventType")
     List<CodeConfigurationTranslationDTO> findAllByIsDeletedCountryCode(@Param("countryCode") String countryCode);

}
