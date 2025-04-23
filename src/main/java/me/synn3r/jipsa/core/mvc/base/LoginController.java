package me.synn3r.jipsa.core.mvc.base;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

  @GetMapping(value={"/", "/login"})
  public String loginPage(Model model, @RequestParam(value="reason", required = false) String reason) {
    model.addAttribute("loginForm", new LoginForm());
    model.addAttribute("reason", reason);
    return "pages/SignIn";
  }
}
