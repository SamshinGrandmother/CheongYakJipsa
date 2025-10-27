package me.synn3r.jipsa.core.mvc.base.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import me.synn3r.jipsa.core.component.security.userdetails.DefaultUserDetails;
import me.synn3r.jipsa.core.mvc.base.domain.SignInForm;
import me.synn3r.jipsa.core.mvc.base.service.SignInService;

@Controller
public class SignInController {
	private final SignInService signInService;

	public SignInController(SignInService signInService) {
		this.signInService = signInService;
	}

	@GetMapping(value = {"/", "/login"})
	public String loginPage(Model model,
		@RequestParam(value = "reason", required = false) String reason) {
		model.addAttribute("loginForm", new SignInForm());
		model.addAttribute("reason", reason);
		return "pages/SignIn";
	}

	@GetMapping("/profile/verify")
	public String profileVerifyPage(Model model,
		@RequestParam(value = "reason", required = false) String reason) {
		model.addAttribute("reason", reason);
		return "pages/VerifyProfile";
	}

	@PostMapping("/profile/verify")
	public ResponseEntity<?> verifyProfile(HttpServletRequest request, @RequestParam String password) {
		try {
			// memberService.verifyMember(request, password);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
				.location(URI.create("/profile/verify?reason=login.notfound")).build();
		}

		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
			.location(URI.create("/profile")).build();
	}

	@GetMapping("/profile")
	public String profilePage(Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		DefaultUserDetails user = (DefaultUserDetails)authentication.getPrincipal();

		// MemberResponse member = memberService.findMemberByUserId(user.getUsername());
		// member.setRole(Role.NORMAL);
		// model.addAttribute("profileForm", member);
		return "pages/Profile";
	}

}
