package com.pcpl.carbon.slweb.Home.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    @RequestMapping(value = "/events/landing")
    public String welcome() { return "landing";
    }
}
