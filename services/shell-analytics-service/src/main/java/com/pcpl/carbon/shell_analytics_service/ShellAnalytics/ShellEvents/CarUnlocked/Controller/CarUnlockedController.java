package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Controller;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.CarUnlockedDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.Model.CarUnlocked;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Repository.CarUnlockedRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Service.CarUnlockedService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.CarUnlocked.Service.CarUnlockedServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/shell-events/car-unlocked")
@Slf4j
public class CarUnlockedController extends AbstractCRUDController<CarUnlocked, CarUnlockedDTO, CarUnlockedRepository, CarUnlockedServiceImpl> {
    @Autowired
    CarUnlockedService carUnlockedService;

    @RequestMapping(value = "/get-car-unlocked-data", method = RequestMethod.POST)
    public ApplicationResponse getByConsumerUuid(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = carUnlockedService.getCarUnlockedData(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @RequestMapping(value = "/get-cars-by-country", method = RequestMethod.POST)
    public ApplicationResponse getByCountry(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = carUnlockedService.getCarUnlockedDataByCountry(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
    @RequestMapping(value = "/get-cars-average",method = RequestMethod.POST)
    public ApplicationResponse getCountryAverages(@RequestBody Map<String,String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        applicationResponse = carUnlockedService.getCountryWiseAverages(formData);
        applicationResponse.setSuccess(true);
        applicationResponse.setMessage("Object fetched successfully!");
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }
}
