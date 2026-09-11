package com.pcpl.carbon.pcplsdk.Generic.Service;

import com.pcpl.carbon.pcplsdk.Common.Dto.FilterDto;
import com.pcpl.carbon.pcplsdk.Common.Dto.FilterType;
import com.pcpl.carbon.pcplsdk.Common.Dto.SortDto;
import com.pcpl.carbon.pcplsdk.Common.Exception.CustomException;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.Utility.AppUtil;
import com.pcpl.carbon.pcplsdk.Common.Utility.MapperUtility;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import jakarta.persistence.Column;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public abstract class AbstractCRUDService<E, D, R extends PCPLCRUDRepository<E>> implements PCPLCRUDService<E, D, R> {
    @Autowired
    R pcplCRUDRepository;
    public abstract E getEntityObject();
    public abstract D getDtoObject();
    public abstract String getUniqueConstraintCheckMethodName();

    @Override
    public D save(D dto) {
        try {
            AppUtil.traceMethodEntry();
//            if (checkDuplicate(dto)) {
//                throw new CustomException("Already exists!!", HttpStatus.CONFLICT);
//            }
            E entity = convertDtoToEntity(dto);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<E>> violations = validator.validate(entity);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            D responseDto = convertEntityToDto(pcplCRUDRepository.save(convertDtoToEntity(dto)));
            AppUtil.traceMethodExit();
            return responseDto;
        } catch (Exception e) {
            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<D> saveAll(List<D> dtos) {
        try {
            AppUtil.traceMethodEntry();
            List<D> responseDtos =  convertEntityListToDtoList(pcplCRUDRepository.saveAll(convertDtoListToEntityList(dtos)));
            AppUtil.traceMethodExit();
            return responseDtos;
        } catch (Exception e) {
            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//
//    public ApplicationResponse getDeleted(ApplicationRequest request) {
//        try {
//            AppUtil.traceMethodEntry();
//            request.getFilterList().add(FilterDto.builder().field("isDeleted").mode(FilterMode.EQUALS).value(1).build());
//            ApplicationResponse response = getAll(request);
//            AppUtil.traceMethodExit();
//            return response;
//        } catch (Exception e) {
//            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
//                    , HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    public ApplicationResponse deleteById(Long id) {
        try {
            AppUtil.traceMethodEntry();
            pcplCRUDRepository.deleteById(id);
            ApplicationResponse response = ApplicationResponse.builder()
                    .data("Successfully deleted")
                    .success(true)
                    .build();
            AppUtil.traceMethodExit();
            return response;
        } catch (Exception e) {
            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ApplicationResponse moveToTrash(Long id) {
        try {
            AppUtil.traceMethodEntry();
            Optional<E> optional = pcplCRUDRepository.findById(id);
            ApplicationResponse response;
            if (optional.isPresent()) {
                E entity = optional.get();
                Field declaredField = entity.getClass().getSuperclass().getDeclaredField("isDeleted");
                boolean accessible = declaredField.isAccessible();
                declaredField.setAccessible(true);
                declaredField.set(entity, 1);
                declaredField.setAccessible(accessible);
                response = ApplicationResponse.builder()
                        .data(convertEntityToDto(pcplCRUDRepository.save(entity)))
                        .success(true)
                        .build();
                AppUtil.traceMethodExit();
            } else {
                response = ApplicationResponse.builder()
                        .data("Record not found")
                        .success(false)
                        .build();
            }
            return response;
        } catch (Exception e) {
            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ApplicationResponse getAll() {
        try {
            AppUtil.traceMethodEntry();
            List<D> dtoList = convertEntityListToDtoList(pcplCRUDRepository.findAll());
            AppUtil.traceMethodExit();
            ApplicationResponse response = ApplicationResponse.builder().success(true)
                    .data(dtoList)
                    .build();
            AppUtil.traceMethodExit();
            return response;
        } catch (Exception e) {
            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ApplicationResponse getById(Long id) {
        try {
            AppUtil.traceMethodEntry();
            ApplicationResponse response = ApplicationResponse.builder().success(true)
                    .data(convertEntityToDto(pcplCRUDRepository.getById(id)))
                    .build();
            AppUtil.traceMethodExit();
            return response;
        } catch (Exception e) {
            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
//    List<E> findAllByIsDeletedAndName(Integer isDeleted, String name){
//        try {
//            AppUtil.traceMethodEntry();
//            List<E> entityList = pcplCRUDRepository.findAllByIsDeletedAndName(isDeleted, name);
//            AppUtil.traceMethodExit();
//            return entityList;
//        } catch (Exception e) {
//                throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
//                        , HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//    }
//    List<E> findAllByIsDeletedAndNameAndIdIsNot(Integer isDeleted, String name, Long id){
//        try {
//            AppUtil.traceMethodEntry();
//            List<E> entityList = pcplCRUDRepository.findAllByIsDeletedAndNameAndIdIsNot(isDeleted, name, id);
//            AppUtil.traceMethodExit();
//            return entityList;
//        } catch (Exception e) {
//            throw new CustomException("Sorry, request failed. | Reason : " + e.getMessage()
//                    , HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    private boolean checkDuplicate(D dto) {
        AppUtil.traceMethodEntry();
        List<E> entityList = new ArrayList<>();
        Object nameObj = invokeMethodWithNoParams(dto, getUniqueConstraintCheckMethodName());
        String name = (nameObj == null) ? "" : nameObj.toString();
        Long id = (Long) invokeMethodWithNoParams(dto, getUniqueConstraintCheckMethodName());
        if (invokeMethodWithNoParams(dto, getUniqueConstraintCheckMethodName()) == null) {
//            entityList = pcplCRUDRepository.findAllByIsDeletedAndName(0, name);
        } else {
//            entityList = pcplCRUDRepository.findAllByIsDeletedAndNameAndIdIsNot(0,name,id);
        }
        AppUtil.traceMethodExit();
        return !entityList.isEmpty();
    }

    protected List<D> convertEntityListToDtoList(List<E> entityList){
        AppUtil.traceMethodEntry();
        List<D> dtoList = new ArrayList<>();
        entityList.stream().forEach( item -> dtoList.add( convertEntityToDto(item) ) );
        AppUtil.traceMethodExit();
        return dtoList;
    }

    public D convertEntityToDto(E entity){
        AppUtil.traceMethodEntry();
        D dto = getDtoObject();
        map(entity, dto);
        AppUtil.traceMethodExit();
        return dto;
    }
    public E convertDtoToEntity(D dto){
        E entity = getEntityObject();
        map(dto, entity);
        AppUtil.traceMethodExit();
        return entity;
    }

    public List<E> convertDtoListToEntityList(List<D> dtoList){
        AppUtil.traceMethodEntry();
        List<E> entityList = new ArrayList<>();
        dtoList.forEach(item -> entityList.add( convertDtoToEntity(item) ) );
        AppUtil.traceMethodExit();
        return entityList;
    }

    public String getCondition(List<FilterDto> filterDTOS) {
        StringBuilder whereCondition = new StringBuilder();
        for (FilterDto filterDTO : filterDTOS) {
            whereCondition.append(" and ").append(getCondition(filterDTO));
        }
        return whereCondition.toString();
    }

    public String getPagination(List<SortDto> sortDTOS, int pageNumber, int pageSize) {
        sortDTOS.sort(Comparator.comparing(SortDto::getPriority));
        StringBuilder sortCondition = new StringBuilder();
        if(!sortDTOS.isEmpty()) {
            sortCondition.append(" order by ");
        }
        for (SortDto sortDTO : sortDTOS) {
            sortCondition.append(sortDTO.getField()).append(" ").append(sortDTO.getOrder());
        }
        sortCondition.append(" LIMIT ").append(pageSize).append(" OFFSET ").append(pageNumber * pageSize);
        return sortCondition.toString();
    }



    public String getCondition(FilterDto filterDto) {
        if (filterDto.getFilterType() == null) {
            switch (filterDto.getMode()) {
                case EQUALS:
                    return filterDto.getField() + " = '" + filterDto.getValue() + "'";
                case NOT_EQUALS:
                    return filterDto.getField() + " != '" + filterDto.getValue() + "'";
                case GREATER_THAN:
                    return filterDto.getField() + " > '" + filterDto.getValue() + "'";
                case LESS_THAN:
                    return filterDto.getField() + " < '" + filterDto.getValue() + "'";
                case CONTAINS:
                    return "lower(" + filterDto.getField() + ") like '%" + filterDto.getValue().toString().toLowerCase() + "%'";
                case IN:
                    return filterDto.getField() + "in (" + filterDto.getValue() + ")";
                case IS_NULL:
                    return filterDto.getField() + "  IS NULL ";
            }
        } else {
            if (filterDto.getFilterType() == FilterType.MULTIPLE_OR) {
                List<String> filters = Arrays.stream(filterDto.getField().split(",")).toList();
                StringBuilder condition = new StringBuilder(" ( ");
                String delim = "";
                for (String filter : filters) {

                    switch (filterDto.getMode()) {
                        case EQUALS:
                            condition.append(delim).append(filter).append(" = '").append(filterDto.getValue()).append("'");
                            break;
                        case NOT_EQUALS:
                            condition.append(delim).append(filter).append(" != '").append(filterDto.getValue()).append("'");
                            break;
                        case GREATER_THAN:
                            condition.append(delim).append(filter).append(" > '").append(filterDto.getValue()).append("'");
                            break;
                        case LESS_THAN:
                            condition.append(delim).append(filter).append(" < '").append(filterDto.getValue()).append("'");
                            break;
                        case CONTAINS:
                            condition.append(delim).append("lower(").append(filter).append(") like '%").append(filterDto.getValue().toString().toLowerCase()).append("%'");
                            break;
                        case IN:
                            condition.append(delim).append(filter).append(" in (").append(filterDto.getValue()).append(")");
                            break;
                        case IS_NULL:
                            condition.append(delim).append(filter).append("  IS NULL ");
                            break;
                    }
                    delim = " OR ";
                }
                return condition + " ) ";
            }
        }
        return "";
    }

    public String getFilterCondition(List<FilterDto> filterDTOS) {
        StringBuilder whereCondition = new StringBuilder();
        for (FilterDto filterDto : filterDTOS) {
            try {
                Field field = getEntityObject().getClass().getDeclaredField(filterDto.getField());
                whereCondition.append(" and ").append(getCondition(field, filterDto));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return whereCondition.toString();
    }

    private String getCondition(Field field,FilterDto filterDto){
        return switch (filterDto.getMode()) {
            case EQUALS -> field.getAnnotation(Column.class).name() + " = '" + filterDto.getValue() + "'";
            case NOT_EQUALS -> field.getAnnotation(Column.class).name() + " != '" + filterDto.getValue() + "'";
            case GREATER_THAN -> field.getAnnotation(Column.class).name() + " > '" + filterDto.getValue() + "'";
            case LESS_THAN -> field.getAnnotation(Column.class).name() + " < '" + filterDto.getValue() + "'";
            case CONTAINS ->
                    "lower(" + field.getAnnotation(Column.class).name() + ") like '%" + filterDto.getValue().toString().toLowerCase() + "%'";
            case IN -> field.getAnnotation(Column.class).name() + "in (" + filterDto.getValue() + ")";
            case IS_NULL -> field.getAnnotation(Column.class).name() + "  IS NULL ";
        };
    }

    private Object map(Object sourceObj, Object destObj){
        try {
            AppUtil.traceMethodEntry();
            ModelMapper mapper = MapperUtility.getInstance();
            mapper.map(sourceObj,destObj);
            AppUtil.traceMethodExit();
            return destObj;
        } catch (Exception exception){
            throw new CustomException("Sorry, request failed. | Reason : " + exception.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    private Object invokeMethodWithNoParams(Object object, String methodName)  {
        try {
            Object result = null;
            Method method = object.getClass().getDeclaredMethod(methodName);
            result = method.invoke(object);
            log.info("{} - {}() output - {}", object, methodName, result);
            AppUtil.traceMethodExit();
            return result;
        }
        catch (NoSuchMethodException exception) {
            log.error("NoSuchMethodException, {}", exception);
            throw new CustomException("Sorry, request failed. | Reason : " + exception.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);

        }
        catch (InvocationTargetException exception) {
            log.error("InvocationTargetException, {}", exception);
            throw new CustomException("Sorry, request failed. | Reason : " + exception.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);

        }
        catch (IllegalAccessException exception) {
            log.error("IllegalAccessException, {}", exception);
            throw new CustomException("Sorry, request failed. | Reason : " + exception.getMessage()
                    , HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}
