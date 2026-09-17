package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CountryCarStatus.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CountryCarStatusDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CountryCarStatus;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CountryCarStatus.Repository.CountryCarStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryCarStatusServiceImpl extends AbstractLazyService<CountryCarStatus, CountryCarStatusDTO, CountryCarStatusRepository> implements CountryCarStatusService {

    @Autowired
    CountryCarStatusRepository countryCarStatusRepository;
    @Override
    public CountryCarStatus getEntityObject() {
        return new CountryCarStatus();
    }

    @Override
    public CountryCarStatusDTO getDtoObject() {
        return new CountryCarStatusDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    @Override
    public ApplicationResponse saveAndUpdateCountryCarStatus(CountryCarStatusDTO countryCarStatusDTO) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try {
            CountryCarStatus countryCarStatus = countryCarStatusRepository.findByCountryAndCarNameAndIsDeleted(countryCarStatusDTO.getCountry(),countryCarStatusDTO.getCarName(),0);
            if (countryCarStatus != null) {
                countryCarStatus.setIsActive(countryCarStatusDTO.getIsActive());

            }
            else  {
                countryCarStatus = new CountryCarStatus();
                countryCarStatus.setCountry(countryCarStatusDTO.getCountry());
                countryCarStatus.setIsActive(countryCarStatusDTO.getIsActive());
                countryCarStatus.setCarName(countryCarStatusDTO.getCarName());
            }
            CountryCarStatus savedStatus = countryCarStatusRepository.save(countryCarStatus);
            applicationResponse.setData(savedStatus);
            applicationResponse.setSuccess(true);
            applicationResponse.setError(null);
        }
        catch (Exception e) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }


        return applicationResponse;
    }
}
