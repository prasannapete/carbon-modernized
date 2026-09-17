package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.Model.UserCountries;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCountriesServiceImpl extends AbstractLazyService<UserCountries, UserCountriesDTO, UserCountriesRepository> implements UserCountriesService {
    @Override
    public UserCountries getEntityObject() {
        return new UserCountries();
    }

    @Override
    public UserCountriesDTO getDtoObject() {
        return new UserCountriesDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }


    public ApplicationResponse multipleMoveToTrash(List<UserCountries> userCountriesList) throws Exception {
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try{
            for(UserCountries userCountries : userCountriesList) {
                super.moveToTrash(userCountries.getId());
            }
            applicationResponse.setSuccess(true);
            applicationResponse.setError(null);
        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }

    @Override
    public List<UserCountriesDTO> saveAll(List<UserCountriesDTO> dtos){
        return super.saveAll(dtos);
    }
}
