package com.pcpl.carbon.pcplsdk.Common.Role.Response;

import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleAccountDTO;
import lombok.Data;

import java.util.List;

@Data
public class RoleAccountResponse {
    private RoleAccountDTO roleAccount;
    private List<RoleAccountDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecords;
    private long recordsFiltered;
    private boolean success;
    private String error;
}
