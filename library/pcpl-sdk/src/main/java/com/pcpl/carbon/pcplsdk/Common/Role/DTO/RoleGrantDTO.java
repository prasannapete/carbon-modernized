package com.pcpl.carbon.pcplsdk.Common.Role.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleGrantDTO {

    private Long id;
    private Long roleId;
    private Long appFeatureId;
}
