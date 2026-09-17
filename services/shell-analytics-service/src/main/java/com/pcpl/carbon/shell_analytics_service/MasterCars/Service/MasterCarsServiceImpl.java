package com.pcpl.carbon.shell_analytics_service.MasterCars.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.DTO.MasterCarsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.Model.MasterCars;
import com.pcpl.carbon.shell_analytics_service.MasterCars.Repository.MasterCarsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MasterCarsServiceImpl extends AbstractLazyService<MasterCars, MasterCarsDTO, MasterCarsRepository> implements MasterCarsService {
    @Autowired
    MasterCarsRepository masterCarsRepository;
    @Override
    public MasterCars getEntityObject() {return new MasterCars();}

    @Override
        public MasterCarsDTO getDtoObject () {return new MasterCarsDTO();}

    @Override
        public String getUniqueConstraintCheckMethodName() {return "getId";}

    @Override
    public ApplicationResponse getCarUnlockedData(Map<String, String> formData) throws Exception {
        ApplicationResponse applicationResponse=  ApplicationResponse.builder().build();
        try {
            String countryCode = formData.get("countryCode");
            List<MasterCarsDTO> carsUnlocked = masterCarsRepository.findByCountryCode(countryCode);
            applicationResponse.setSuccess(true);
            applicationResponse.setData(carsUnlocked);
            applicationResponse.setMessage("Object fetched successfully!");
            applicationResponse.setError(null);
        }catch (Exception e){
            applicationResponse.setSuccess(false);
            applicationResponse.setError(e.getMessage());
        }
        return applicationResponse;
    }

    @Override
    public ApplicationResponse updateCarStatus(MasterCarsDTO masterCarsDTO) throws Exception {
        ApplicationResponse applicationResponse=  ApplicationResponse.builder().build();
        try {
            MasterCars  masterCars = masterCarsRepository.findById(masterCarsDTO.getId().intValue());
            if(masterCars !=null){
                masterCars.setIsActive(masterCarsDTO.getIsActive());
            }
            MasterCars savedStatus = masterCarsRepository.save(masterCars);
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

