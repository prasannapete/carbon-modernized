package com.pcpl.carbon.pcplsdk.Common.Dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Data
@Builder
public class SpecificationDto {
    private Specification specification;
    private Pageable paging;
}
