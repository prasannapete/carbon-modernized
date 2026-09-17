package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.OfferCodeConfiguration.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferCodeConfigurationDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.OfferCodeConfiguration;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OfferCodeConfigurationRepository extends PCPLCRUDRepository<OfferCodeConfiguration> {


    Optional<OfferCodeConfiguration> findByCountryCodeAndIsDeletedAndEventType(String countryCode, int isDeleted, Integer eventCode);

    Optional<OfferCodeConfiguration> findAllByIsDeletedAndEventTypeAndCountryCode(int i, int eventType, String countryCode);

    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.OfferCodeConfigurationDTO(" +
                    "offerCodeConfiguration.id, " +
                    "offerCodeConfiguration .countryCode, " +
                    "offerCodeConfiguration.country, " +
                    "offerCodeConfiguration.eventType, " +
                    "offerCodeConfiguration.offerCode, " +
                    "offerCodeConfiguration.validFrom," +
                    "offerCodeConfiguration.validUntil," +
                    "offerCodeConfiguration.isActive," +
                    "offerCodeConfiguration.points, " +
                    "null," +
                    "offerCodeConfiguration.validityPeriod," +
                    "null," +
                    "false " +
                    " )" +
                    " FROM OfferCodeConfiguration as offerCodeConfiguration" +
                    " WHERE offerCodeConfiguration.countryCode= :countryCode and offerCodeConfiguration.isDeleted=0"
    )
    List<OfferCodeConfigurationDTO> findAllByIsDeletedAndCountryCode(String countryCode);
}
