package com.pcpl.carbon.pcplsdk.Common.Response;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApplicationResponse {
    private Object data;
    private boolean success;
    @Builder.Default
    private Integer code = HttpStatus.OK.value();
    private String message;
    private String error;
    private int totalPages;
    private Long recordsTotal;
    private int currentRecords;
    private Long recordsFiltered;
    private String excelUploadPath;
}
