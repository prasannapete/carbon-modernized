package com.pcpl.carbon.shell_analytics_service.Mailer;

import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;

@Service
public class SMTPMailClient implements EmailClient {


    @Autowired
    SrlAnalyticsConfig srlAnalyticsConfig;

    @Autowired
    JavaMailSender mailSender;

    @Override
    public boolean sendEmail(EmailParameters emailParameters) throws Exception {
        MimeMessagePreparator messagePreparation = mimeMessage -> {
            MimeMessageHelper messageHelper;
            if (!emailParameters.getAttachments().isEmpty()) {
                messageHelper = new MimeMessageHelper(mimeMessage, true);
            } else {
                messageHelper = new MimeMessageHelper(mimeMessage);
            }
            messageHelper.setFrom(srlAnalyticsConfig.getMailFromEmailId());
            messageHelper.setTo(emailParameters.getToEmail());
            messageHelper.setSubject(emailParameters.getSubject());
            messageHelper.setText(emailParameters.getMessage(), true);
            if (!emailParameters.getCcEmails().isEmpty()) {
                messageHelper.setCc(emailParameters.getCcEmails().toArray(new String[emailParameters.getCcEmails().size()]));
            }
            for (HashMap<String, String> attachmentPath : emailParameters.getAttachments()) {
                for (String name : attachmentPath.keySet()) {
                    messageHelper.addAttachment(name, new File(attachmentPath.get(name)));
                }
            }
        };
        try {
            mailSender.send(messagePreparation);
            return true;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
