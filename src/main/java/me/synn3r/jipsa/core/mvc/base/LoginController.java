package me.synn3r.jipsa.core.mvc.base;

import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.service.MemberService;
import me.synn3r.jipsa.core.component.security.DefaultUserDetails;
import me.synn3r.jipsa.core.component.security.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

  private final MemberService memberService;

  public LoginController(MemberService memberService) {
    this.memberService = memberService;
  }

  @GetMapping(value = {"/", "/login"})
  public String loginPage(Model model,
    @RequestParam(value = "reason", required = false) String reason) {
    model.addAttribute("loginForm", new LoginForm());
    model.addAttribute("reason", reason);
    return "pages/SignIn";
  }

  @GetMapping("/signup")
  public String registerPage(Model model) {

    MemberRequest member = new MemberRequest();
    member.setRole(Role.NORMAL);
    model.addAttribute("signupForm", member);
    return "pages/SignUp";
  }

  @GetMapping("/profile")
  public String profilePage(Model model) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    DefaultUserDetails user = (DefaultUserDetails) authentication.getPrincipal();

    MemberResponse member = memberService.findMemberByUserId(user.getUsername());
    member.setRole(Role.NORMAL);
    model.addAttribute("profileForm", member);
    return "pages/Profile";
  }

}
