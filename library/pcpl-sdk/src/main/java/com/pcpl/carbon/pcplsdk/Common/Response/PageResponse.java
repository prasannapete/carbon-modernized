package com.pcpl.carbon.pcplsdk.Common.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse {
    private Object data;
    private int pageNumber;
    private int pageSize;
    private int recordsFiltered;
    private Long totalRecords;
    private int totalPages;
}
