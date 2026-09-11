package com.pcpl.carbon.pcplsdk.Common.AppFeature.Response;

import com.pcpl.carbon.pcplsdk.Common.AppFeature.DTO.AppFeatureDTO;
import com.pcpl.carbon.pcplsdk.Common.AppFeature.DTO.AppFeatureTreeDTO;
import lombok.Data;

import java.util.List;

@Data
public class AppFeatureResponse {
    private AppFeatureDTO appFeature;
    private List<AppFeatureDTO> data;
    private List<AppFeatureTreeDTO> treeData;
    private long totalPages;
    private long recordsTotal;
    private long currentRecords;
    private long recordsFiltered;
    private boolean success;
    private String error;
}
