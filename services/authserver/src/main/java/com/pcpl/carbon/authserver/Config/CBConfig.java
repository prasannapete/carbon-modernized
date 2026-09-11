package com.pcpl.carbon.authserver.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties("com.pcpl.carbon")
@Data
public class CBConfig {
    public String webPageUrl;
    public String assetPath;
    public String jsPath;
    public String bowerPath;
    public String homeUrl;
    public Boolean seoNoIndex;
    public String issuerUrl;
    public String loginRedirectURL;
}
