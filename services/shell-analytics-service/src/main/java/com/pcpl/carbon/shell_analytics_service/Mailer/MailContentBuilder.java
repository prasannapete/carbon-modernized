package com.pcpl.carbon.shell_analytics_service.Mailer;

import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.shell_analytics_service.config.SrlAnalyticsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class MailContentBuilder {
    private TemplateEngine templateEngine;

    @Autowired
    SrlAnalyticsConfig srlAnalyticsConfig;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildResetPasswordEmail(User user) {

        Context context = new Context();
        context.setVariable("user", user);

        var userFullName = user.getFirstName();

        if(user.getLastName() != null){
            userFullName += " " + user.getLastName();
        }

        context.setVariable("userFullName", userFullName);

        String invitationMessage = "You have been invited to Join Carbon Dashboard.";

        String resetPasswordURL =
                srlAnalyticsConfig.getAppBaseUrl()
                        + "/reset-password?id="
                        + user.getId();

        String logoUrl = "https://srl-dev-dashboard.prapticonsulting.com/static/media/shell-icon.be68693de0a08565eb45.png";

        if(user.getIsPlayMobilUser() == 1){

            invitationMessage = "You have been invited to Join Play Mobil.";

            resetPasswordURL =
                    srlAnalyticsConfig.getPlayMobilBaseUrl()
                            + "/reset-password?id="
                            + user.getId();

            logoUrl = "https://leaderboard.carbon12011ar.com/images/logos/carbonLogo.png";
        }

        context.setVariable("invitationMessage", invitationMessage);
        context.setVariable("resetPasswordURL", resetPasswordURL);
        context.setVariable("logoUrl", logoUrl);

        return templateEngine.process("user-invitation", context);
    }
}
