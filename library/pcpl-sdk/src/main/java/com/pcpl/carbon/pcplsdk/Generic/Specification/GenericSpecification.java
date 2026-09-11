package com.pcpl.carbon.pcplsdk.Generic.Specification;

import com.pcpl.carbon.pcplsdk.Common.Dto.FilterDto;
import com.pcpl.carbon.pcplsdk.Common.Dto.SortDto;
import com.pcpl.carbon.pcplsdk.Common.Dto.SpecificationDto;
import com.pcpl.carbon.pcplsdk.Common.Request.ApplicationRequest;
import com.pcpl.carbon.pcplsdk.Common.Utility.AppUtil;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericSpecification<E> implements Specification<E> {

    private FilterDto filterDto;
    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        switch (filterDto.getMode()) {
            case EQUALS:
                return builder.equal(getFieldExpression(root, builder), filterDto.getValue().toString());
            case NOT_EQUALS:
                return builder.notEqual(getFieldExpression(root, builder), filterDto.getValue().toString());
            case GREATER_THAN:
                return builder.greaterThanOrEqualTo(getFieldExpression(root, builder), filterDto.getValue().toString());
            case LESS_THAN:
                return builder.lessThanOrEqualTo(getFieldExpression(root, builder), filterDto.getValue().toString());
            case CONTAINS:
                return builder.like(builder.upper(root.get(filterDto.getField()).as(String.class)),
                        String.valueOf("%" + filterDto.getValue() + "%").toUpperCase());
            case IN:
                return builder.in(getFieldExpression(root, builder)).in(filterDto.getValue());
            case IS_NULL:
                return builder.isNull(getFieldExpression(root, builder));
        }
        log.error("Error Mode not implemented {} ", filterDto.getMode());
        return null;
    }

    private Expression<String> getFieldExpression(Root<E> root, CriteriaBuilder builder) {

        if (root.get(filterDto.getField()).getJavaType() == LocalDateTime.class) {
            return builder.upper(root.get(filterDto.getField()).as(String.class));
        } else {
            return root.get(filterDto.getField());
        }
    }

    public SpecificationDto getSortFilterCriteria(ApplicationRequest request) {
        List<Sort.Order> orders = new ArrayList<>();
        if (request.getSortList() != null && !request.getSortList().isEmpty()) {
            request.getSortList().sort(Comparator.comparing(SortDto::getPriority));
            request.getSortList().forEach(sort -> {
                orders.add(new Sort.Order(sort.getOrder(), sort.getField()));
            });
        }
        Pageable paging = PageRequest.of(request.getPageNumber(), request.getSize(), Sort.by(orders));

        Specification<E> filterSpecification = this.getFilterSpecification(request);

        return SpecificationDto.builder()
                .specification(filterSpecification)
                .paging(paging)
                .build();
    }


    private Specification<E> getFilterSpecification(ApplicationRequest request) {

        Specification<E> finalFilterSpecification = null;
        Specification<E> spec = null;

        if (!AppUtil.isListEmpty(request.getFilterList())) {
            List<String> distinctFilterColumnList = request.getFilterList().stream()
                                        .map(FilterDto::getField).distinct()
                                        .collect(Collectors.toList());

            for (String column : distinctFilterColumnList) {
                Specification<E> columnSpecs = null;
                List<FilterDto> columnfilterDtoList = request.getFilterList().stream()
                                                .filter(filter -> filter.getField().equalsIgnoreCase(column))
                                                .collect(Collectors.toList());

                for (FilterDto filterDto : columnfilterDtoList) {
                    spec = new GenericSpecification<E>(filterDto);
                    if (columnSpecs == null) {
                        columnSpecs = Specification.where(spec);
                    } else {
                        columnSpecs = columnSpecs.or(spec);
                    }
                }
                if (finalFilterSpecification == null) {
                    finalFilterSpecification = Specification.where(columnSpecs);
                } else {
                    finalFilterSpecification = finalFilterSpecification.and(columnSpecs);
                }
            }
        }
        return finalFilterSpecification;
    }

}
