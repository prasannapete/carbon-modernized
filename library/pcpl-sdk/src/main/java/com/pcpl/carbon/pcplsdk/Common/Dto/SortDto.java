package com.pcpl.carbon.pcplsdk.Common.Dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
@Builder
public class SortDto {
    private String field;
    private Integer priority;
    private Sort.Direction order;
}
