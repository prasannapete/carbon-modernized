package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.Model.UserCountries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCountriesRepository extends PCPLCRUDRepository<UserCountries> {
    List<UserCountries> findAllByIsDeletedAndUserId(int isDeleted,Long userId);
    List<UserCountries> findAllByIsDeletedAndCountryIdIn(int isDeleted,List<Long> countryIds);
    @Query("SELECT new com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO (" +
            "userCountries.id," +
            "userCountries.userId," +
            "userCountries.countryId," +
            "userCountries.tenantId," +
            "userCountries.createdBy," +
            "userCountries.creationTime," +
            "userCountries.lastModifiedBy," +
            "userCountries.lastModifiedTime," +
            "userCountries.isDeleted," +
            "userCountries.deletedBy," +
            "userCountries.deletedTime," +
            "country.countryName," +
            "country.country" +
            ")" +
            " FROM UserCountries userCountries" +
            " LEFT JOIN Country country on country.id = userCountries.countryId and country.isDeleted=0" +
            " WHERE userCountries.isDeleted =0 and userCountries.userId = :userId")
    List<UserCountriesDTO> getUserCountriesByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT (userCountries.countryId) " +
            "FROM UserCountries userCountries " +
            "WHERE userCountries.isDeleted = 0 and userCountries.userId = :userId" )
    List<Long> getCountryIdsByUserId(@Param("userId") Long userId);
}

