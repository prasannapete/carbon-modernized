package com.pcpl.carbon.authserver.User.Controller;

import com.pcpl.carbon.authserver.Config.CBConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    CBConfig cbConfig;

    @RequestMapping("/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response) {
        // token can be revoked here if needed
        String referer = cbConfig.getHomeUrl();
        try {
            new SecurityContextLogoutHandler().logout(request, null, null);
            response.sendRedirect(referer);
        }catch (Exception ex){
        }
    }

    @GetMapping("/forgot-password-screen")
    public String forgotPassword(HttpServletRequest request) {
        return "redirect:" + cbConfig.getHomeUrl() + "/forgot-password-screen";
    }
}
