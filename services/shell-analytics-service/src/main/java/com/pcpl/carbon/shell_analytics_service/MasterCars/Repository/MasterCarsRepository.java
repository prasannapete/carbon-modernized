package com.pcpl.carbon.shell_analytics_service.MasterCars.Repository;

import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.DTO.MasterCarsDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.Model.MasterCars;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MasterCarsRepository extends PCPLCRUDRepository<MasterCars> {
    @Query("Select DISTINCT new com.pcpl.carbon.pcplsdk.ShellAnalytics.MasterCars.DTO.MasterCarsDTO(" +
    "c.id,c.tenantId,c.country,c.countryCode,c.carName,c.isActive ) "+
    "From MasterCars c "+
    "WHERE c.countryCode=:countryCode")
    List<MasterCarsDTO> findByCountryCode(String countryCode);

    MasterCars findById(Integer id);
}
