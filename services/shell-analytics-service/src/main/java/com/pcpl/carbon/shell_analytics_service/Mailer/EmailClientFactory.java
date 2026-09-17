package com.pcpl.carbon.shell_analytics_service.Mailer;

import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailClientFactory {

    @Autowired
    SrlAnalyticsConfig srlAnalyticsConfig;

    @Autowired
    SendGridMailClient sendGridMailClient;

    @Autowired
    SMTPMailClient smtpMailClient;


    public EmailClient emailClient() {
        switch (srlAnalyticsConfig.mailClientType) {
            case 1:
                return sendGridMailClient;
            case 2:
                return smtpMailClient;
            default:
                return sendGridMailClient;
        }
    }
}
