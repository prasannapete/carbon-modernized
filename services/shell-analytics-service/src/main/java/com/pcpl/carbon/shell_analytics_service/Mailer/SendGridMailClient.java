package com.pcpl.carbon.shell_analytics_service.Mailer;

import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@Service
public class SendGridMailClient implements EmailClient {

    @Autowired
    SendGrid sendGrid;

    @Autowired
    SrlAnalyticsConfig srlAnalyticsConfig;


    @Override
    public boolean sendEmail(EmailParameters emailParameters) throws Exception {
        Email from = new Email(srlAnalyticsConfig.getMailFromEmailId());
        Email to = new Email(emailParameters.getToEmail());
        Content content = new Content("text/html", emailParameters.getMessage());
        Mail mail = new Mail(from, emailParameters.getSubject(), to, content);
        for (String key : emailParameters.getCustomArgs().keySet()) {
            if (!mail.getPersonalization().isEmpty()) {
                mail.getPersonalization().get(0).addCustomArg(
                        key,
                        emailParameters.getCustomArgs().get(key));
            } else {
                Personalization personalization = new Personalization();
                personalization.addCustomArg(
                        key,
                        emailParameters.getCustomArgs().get(key));
                mail.addPersonalization(personalization);
            }
        }
        for (HashMap<String, String> attachmentPath : emailParameters.getAttachments()) {
            for (String name : attachmentPath.keySet()) {
                try (final InputStream inputStream = Files.newInputStream(Paths.get(attachmentPath.get(name)))) {
                    final Attachments attachments = new Attachments
                            .Builder(name, inputStream)
                            .build();
                    mail.addAttachments(attachments);
                }
            }
        }
        if(!emailParameters.getCcEmails().isEmpty()) {
            if (!mail.getPersonalization().isEmpty()) {
                for (String ccEmail : emailParameters.getCcEmails()) {
                    mail.getPersonalization().get(0).addCc(new Email(ccEmail));
                }
            } else {
                Personalization personalization = new Personalization();
                for (String ccEmail : emailParameters.getCcEmails()) {
                    mail.getPersonalization().get(0).addCc(new Email(ccEmail));
                }
                mail.addPersonalization(personalization);
            }
        }
        Request request = new Request();
        Response response;
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        response = sendGrid.api(request);
        return response.getStatusCode() == 200;

    }

}
