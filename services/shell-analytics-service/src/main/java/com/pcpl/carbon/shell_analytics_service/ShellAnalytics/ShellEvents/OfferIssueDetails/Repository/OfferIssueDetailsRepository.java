package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferIssueDetails.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferIssueDetailsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferIssueDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferIssueDetailsRepository extends PCPLCRUDRepository<OfferIssueDetails> {
    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferIssueDetailsDTO(" +
                    "offerIssueDetails.id, " +
                    "offerIssueDetails .consumerUuid, " +
                    "offerIssueDetails.eventType, " +
                    "offerIssueDetails.offerCode, " +
                    "offerIssueDetails.issuedDate, " +
                    "offerIssueDetails.countryCode, " +
                    "null," +
                    "offerCodeConfiguration.points, " +
                    "offerIssueDetails.gameId " +
                    " )" +
                    " FROM OfferIssueDetails as offerIssueDetails" +
                    " LEFT JOIN OfferCodeConfiguration offerCodeConfiguration on offerCodeConfiguration.offerCode = offerIssueDetails.offerCode and offerCodeConfiguration.countryCode = offerIssueDetails.countryCode and offerCodeConfiguration.eventType = offerIssueDetails.eventType" +
                    " LEFT join CodeConfigurationTranslation  offerCodeTranslation on offerCodeTranslation.codeConfigurationId = offerCodeConfiguration.id" +
                    " WHERE offerIssueDetails.consumerUuid= :consumerUuid AND offerIssueDetails.isDeleted= :isDeleted AND offerIssueDetails.countryCode= :countryCode"
    )
    List<OfferIssueDetailsDTO> findAllByConsumerUuidAndIsDeleted(@Param("consumerUuid") String consumerUuid,@Param("isDeleted") int isDeleted,@Param("countryCode") String countryCode);

    Optional<OfferIssueDetails> findFirstByConsumerUuidAndOfferCodeAndIsDeletedAndCountryCodeOrderByCreationTime(String consumerUuid, String offerCode, int i, String countryCode);
}
