package com.pcpl.carbon.kitkat_service.Config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties("com.pcpl.carbon")
public class PCPLConfig {
    public String excelUploadPath;
}
