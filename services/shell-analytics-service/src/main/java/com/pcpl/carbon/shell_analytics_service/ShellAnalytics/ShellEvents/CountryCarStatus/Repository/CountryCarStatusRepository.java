package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CountryCarStatus.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CountryCarStatus;

public interface CountryCarStatusRepository extends PCPLCRUDRepository<CountryCarStatus> {
    CountryCarStatus findByCountryAndCarNameAndIsDeleted(String countryName,String Country,Integer isDeleted);
}
