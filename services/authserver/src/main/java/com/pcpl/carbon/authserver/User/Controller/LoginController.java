package com.pcpl.carbon.authserver.User.Controller;

import com.pcpl.carbon.authserver.Config.CBConfig;
import com.pcpl.carbon.authserver.Tenant.Service.TenantService;
import com.pcpl.carbon.authserver.User.Model.User;
import com.pcpl.carbon.authserver.User.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


import java.util.Optional;

@Controller
@Slf4j
public class LoginController {
    @Autowired
    CBConfig cbConfig;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TenantService tenantService;

    @GetMapping(value = "/")
    public String login(Model model) {
        return "redirect:/login";
    }

    /**
     * Chrome DevTools auto-probes this path when it is open, looking for an "automatic
     * workspace folders" config. There is nothing to serve, so return 204 No Content
     * instead of letting it fall through to a 404 Whitelabel error / logged stack trace.
     */
    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void chromeDevtoolsProbe() {
    }
    @RequestMapping(value = "/success")
    public String loginSuccess(Model model) {
        return "redirect:" +cbConfig.getLoginRedirectURL();
    }

    @GetMapping("/user/me")
    public User getMe(){
        JwtAuthenticationToken authentication  =
                (JwtAuthenticationToken) SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        String userName = authentication.getName();
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        User user;
        user = new User();
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }
        return user;
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        HttpSession session) {
        if (error != null) {
            model.addAttribute("showError", true);
            model.addAttribute("errorMsg", "Invalid user name or password");
        } else {
            model.addAttribute("showError", false);
            model.addAttribute("errorMsg", "");
        }
        model.addAttribute("seoNoIndex", cbConfig.getSeoNoIndex());
//        model.addAttribute("forgotPasswordURL",  slConfig.getHomeUrl() + "pirelli/user/forgot-password-screen");

        if (logout != null)
            model.addAttribute("msg", "You have been logged out successfully.");
        // Pick the tenant-specific login screen based on the request sub-domain,
        // falling back to the default screen when no tenant matches.
        return tenantService.resolveLoginTemplate(request);
    }
}
