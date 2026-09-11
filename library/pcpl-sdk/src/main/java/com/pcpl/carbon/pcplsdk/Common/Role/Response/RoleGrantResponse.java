package com.pcpl.carbon.pcplsdk.Common.Role.Response;

import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleGrantDTO;
import lombok.Data;

import java.util.List;

@Data
public class RoleGrantResponse {
    private RoleGrantDTO roleGrant;
    private List<RoleGrantDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecords;
    private long recordsFiltered;
    private boolean success;
    private String error;
}
