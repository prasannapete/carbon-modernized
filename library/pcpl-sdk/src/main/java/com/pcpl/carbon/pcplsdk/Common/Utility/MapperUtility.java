package com.pcpl.carbon.pcplsdk.Common.Utility;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public final class MapperUtility {
    private static ModelMapper mapper;
    private MapperUtility(){};

    public static ModelMapper getInstance(){
        if (mapper == null) {
            mapper = new ModelMapper();
        }
        return mapper;
    }
}
