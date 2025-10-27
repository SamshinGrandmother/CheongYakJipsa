package me.synn3r.jipsa.core.mvc.base.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import me.synn3r.jipsa.core.mvc.base.domain.SignUpForm;
import me.synn3r.jipsa.core.mvc.base.service.SignUpService;

@Controller
public class SignUpController {
	private final SignUpService memberService;

	public SignUpController(SignUpService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("/signup")
	public String registerPage(Model model) {

		SignUpForm signupForm = new SignUpForm();
		model.addAttribute("signupForm", signupForm);
		return "pages/SignUp";
	}

	@PostMapping("/signup")
	public String register(@Validated @ModelAttribute("signupForm") SignUpForm signupForm,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "pages/SignUp";
		}
		memberService.signUp(signupForm);

		return "redirect:/login?reason=signup.success";
	}
}

