package com.pcpl.carbon.shell_analytics_service.Mailer;

public interface EmailClient {

    boolean sendEmail(EmailParameters emailParameters) throws Exception;
}
