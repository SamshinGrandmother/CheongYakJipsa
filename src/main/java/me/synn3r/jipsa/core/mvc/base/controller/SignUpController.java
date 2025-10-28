package me.synn3r.jipsa.core.mvc.base.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
	private final MessageSource messageSource;

	public SignUpController(SignUpService memberService, MessageSource messageSource) {
		this.memberService = memberService;
		this.messageSource = messageSource;
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
		try {
			memberService.signUp(signupForm);
		} catch (IllegalArgumentException e) {
			String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
			bindingResult.reject("signUp.error", message);
			return "pages/SignUp";
		}

		return "redirect:/login?reason=signup.success";
	}
}

