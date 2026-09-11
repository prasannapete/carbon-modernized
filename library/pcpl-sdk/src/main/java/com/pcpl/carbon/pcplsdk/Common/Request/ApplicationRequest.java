package com.pcpl.carbon.pcplsdk.Common.Request;


import com.pcpl.carbon.pcplsdk.Common.Dto.FilterDto;
import com.pcpl.carbon.pcplsdk.Common.Dto.SortDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApplicationRequest {
    private Integer pageNumber;
    private Integer size = 10;
    private List<FilterDto> filterList;
    private List<SortDto> sortList;
    private int draw;
    private int status;
}
