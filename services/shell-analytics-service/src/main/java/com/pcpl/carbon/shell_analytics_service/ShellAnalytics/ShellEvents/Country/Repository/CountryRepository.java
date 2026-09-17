package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Country.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.Country;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends PCPLCRUDRepository<Country> {
    List<Country> findAllByIsDeletedOrderByCountryName(int isDeleted);
    Optional<Country> findByCountryNameAndIsDeleted(String countryName, int isDeleted);
}
