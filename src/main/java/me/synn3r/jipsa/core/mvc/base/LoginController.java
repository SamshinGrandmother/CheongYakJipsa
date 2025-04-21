package me.synn3r.jipsa.core.mvc.base;

import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.component.security.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
  @GetMapping(value={"/", "/login"})
  public String loginPage(Model model) {
    model.addAttribute("loginForm", new LoginForm());
    return "pages/SignIn";
  }

  @GetMapping("/signup")
  public String registerPage(Model model) {

    MemberRequest member = new MemberRequest();
    member.setRole(Role.NORMAL);
    model.addAttribute("signupForm", member);
    return "pages/SignUp";
  }


}
