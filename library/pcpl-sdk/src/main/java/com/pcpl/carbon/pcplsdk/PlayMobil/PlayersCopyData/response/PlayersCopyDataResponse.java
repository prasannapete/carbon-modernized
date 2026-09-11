package com.pcpl.carbon.pcplsdk.PlayMobil.PlayersCopyData.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class PlayersCopyDataResponse {

    private Object playersCopyData;
    private boolean success;
    @Builder.Default
    private Integer code = HttpStatus.OK.value();
    private String message;
    private String error;
    private int totalPages;
    private Long recordsTotal;
    private int currentRecords;
    private Long recordsFiltered;
}
