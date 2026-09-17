package com.pcpl.carbon.shell_analytics_service.MasterCars.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.DTO.MasterCarsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.Model.MasterCars;
import com.pcpl.carbon.shell_analytics_service.MasterCars.Repository.MasterCarsRepository;
import com.pcpl.carbon.shell_analytics_service.MasterCars.Service.MasterCarsService;
import com.pcpl.carbon.shell_analytics_service.MasterCars.Service.MasterCarsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@RestController
@RequestMapping(value="/srl/master-cars")
@Slf4j
public class MasterCarsController extends AbstractCRUDController<MasterCars, MasterCarsDTO, MasterCarsRepository, MasterCarsServiceImpl> {
    @Autowired
    MasterCarsService masterCarsService;

    @RequestMapping(value = "/get-cars-unlocked",method = RequestMethod.POST)
    public ApplicationResponse getCarsUnlocked(@RequestBody Map<String,String> formData) throws  Exception{
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = masterCarsService.getCarUnlockedData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/update-status",method = RequestMethod.POST)
    public ApplicationResponse updateStatus(@RequestBody MasterCarsDTO masterCarsDTO) throws Exception{
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        applicationResponse = masterCarsService.updateCarStatus(masterCarsDTO);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object updated successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
