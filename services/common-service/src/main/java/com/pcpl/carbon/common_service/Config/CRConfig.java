package com.pcpl.carbon.commonservice.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Data
@ConfigurationProperties("com.pcpl.carbon")
public class CRConfig {
    public String playFabURL;
}
