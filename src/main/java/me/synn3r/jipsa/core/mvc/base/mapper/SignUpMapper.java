package me.synn3r.jipsa.core.mvc.base.mapper;

import org.mapstruct.Mapper;

import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.mvc.base.domain.SignUpForm;

@Mapper
public interface SignUpMapper {
	Member toMember(SignUpForm signUpForm, String encodedPassword);
}
