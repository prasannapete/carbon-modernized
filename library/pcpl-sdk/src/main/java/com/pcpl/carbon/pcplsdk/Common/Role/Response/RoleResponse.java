package com.pcpl.carbon.pcplsdk.Common.Role.Response;

import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleDTO;
import lombok.Data;

import java.util.List;

@Data
public class RoleResponse {
    private RoleDTO role;
    private List<RoleDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecords;
    private long recordsFiltered;
    private boolean success;
    private String error;
}
