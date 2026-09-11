package com.pcpl.carbon.pcplsdk.Common.User.Response;

import com.pcpl.carbon.pcplsdk.Common.User.DTO.UserDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private UserDTO user;
    private List<UserDTO> data;
    private long totalPages;
    private long recordsTotal;
    private long currentRecords;
    private long recordsFiltered;
    private boolean success;
    private String error;
}
