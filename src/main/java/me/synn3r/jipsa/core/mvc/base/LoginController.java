package me.synn3r.jipsa.core.mvc.base;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
  @GetMapping(value={"/", "/login"})
  public String loginPage(Model model) {
    model.addAttribute("loginForm", new LoginForm());
    return "pages/SignUp";
  }
}
