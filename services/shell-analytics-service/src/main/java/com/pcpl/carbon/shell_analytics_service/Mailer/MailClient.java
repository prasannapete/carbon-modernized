package com.pcpl.carbon.shell_analytics_service.Mailer;

import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import com.sendgrid.SendGrid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailClient {
    @Autowired
    MailContentBuilder mailContentBuilder;

    @Autowired
    SrlAnalyticsConfig srlAnalyticsConfig;

    JavaMailSender mailSender;
    private SendGrid sendGrid;
    private EmailClientFactory emailClientFactory;
    private EmailClient mailClient;


    @Autowired
    public MailClient(SendGrid sendGrid, EmailClientFactory emailClientFactory) {
        this.sendGrid = sendGrid;
        this.emailClientFactory = emailClientFactory;
        this.mailClient = emailClientFactory.emailClient();
    }

    public void sendInviteUserEmail(User user) throws Exception{

        String subject = "Shell Analytics User Invitation";

        if(user.getIsPlayMobilUser() == 1){
            subject = "Play Mobil User Invitation";
        }

        EmailParameters emailParameters = new EmailParameters(subject, user.getEmailAddress(), mailContentBuilder.buildResetPasswordEmail(user));
        try {
            mailClient.sendEmail(emailParameters);
        } catch (MailException e) {
            log.error(e.toString());
            e.printStackTrace();
        }
    }

}
