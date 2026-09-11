package com.pcpl.carbon.pcplsdk.Common.Dto;

import com.pcpl.carbon.pcplsdk.Common.Enums.FilterMode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterDto {
    private String field;
    private Object value;
    private FilterMode mode;
    private FilterType filterType;
}
