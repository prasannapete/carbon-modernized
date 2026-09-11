package com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenDTO {

    private Long id;
    private Long tenantId;
    private String consumerUuid;
    private String refreshToken;
    private String firstName;
    private String lastName;


}
