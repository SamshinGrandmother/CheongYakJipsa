package me.synn3r.jipsa.core.mvc.base.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.component.security.Role;
import me.synn3r.jipsa.core.mvc.base.domain.SignUpForm;

@Mapper
public interface SignUpMapper {

	@Mappings(value = {
		@Mapping(source = "signUpForm.userId", target = "userId"),
		@Mapping(source = "signUpForm.name", target = "name"),
		@Mapping(source = "signUpForm.email", target = "email"),
		@Mapping(source = "encodedPassword", target = "password"),
		@Mapping(source = "role", target = "role")
	})
	Member toMember(SignUpForm signUpForm, String encodedPassword, Role role);
}
