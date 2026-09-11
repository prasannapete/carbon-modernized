package com.pcpl.carbon.pcplsdk.Generic.Service;

import com.pcpl.carbon.pcplsdk.Common.Dto.FilterDto;
import com.pcpl.carbon.pcplsdk.Common.Dto.SpecificationDto;
import com.pcpl.carbon.pcplsdk.Common.Enums.FilterMode;
import com.pcpl.carbon.pcplsdk.Common.Exception.CustomException;
import com.pcpl.carbon.pcplsdk.Common.Request.ApplicationRequest;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.Response.PageResponse;
import com.pcpl.carbon.pcplsdk.Common.Utility.AppUtil;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import com.pcpl.carbon.pcplsdk.Generic.Specification.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractLazyService<E, D, R extends PCPLCRUDRepository<E>> extends AbstractCRUDService<E, D, R> {

    public ApplicationResponse getAll(ApplicationRequest request) {
        try {
            AppUtil.traceMethodEntry();
            GenericSpecification<E> specification = new GenericSpecification<>();
            if (request.getFilterList().stream().noneMatch(filter -> filter.getField().equals("isDeleted"))) {
                request.getFilterList().add(FilterDto.builder().field("isDeleted").mode(FilterMode.EQUALS).value(0).build());
            }
            SpecificationDto specDto = specification.getSortFilterCriteria(request);
            Page<E> pages = pcplCRUDRepository.findAll( (Specification<E>) specDto.getSpecification(), specDto.getPaging() );
            PageResponse page = PageResponse.builder()
                    .data(convertEntityListToDtoList(pages.getContent()))
                    .pageSize(pages.getPageable().getPageSize())
                    .recordsFiltered(pages.getNumberOfElements())
                    .totalRecords(pages.getTotalElements())
                    .pageNumber(pages.getPageable().getPageNumber())
                    .build();
            ApplicationResponse response = ApplicationResponse.builder().success(true).data(page).build();
            response.setCurrentRecords(pages.getNumberOfElements());
            response.setRecordsTotal(pages.getTotalElements());
            response.setRecordsFiltered(pages.getTotalElements());
            response.setTotalPages(pages.getTotalPages());
            AppUtil.traceMethodExit();
            return response;
        } catch (Exception e) {
            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
