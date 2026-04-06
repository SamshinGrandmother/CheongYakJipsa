package me.synn3r.jipsa.core.api.member.entity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import me.synn3r.jipsa.core.api.member.domain.MemberRegistrationRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;

@Mapper
public interface MemberMapper {

	@Mappings({
		@Mapping(source = "memberRequest.name", target = "name"),
		@Mapping(source = "memberRequest.email", target = "email"),
		@Mapping(source = "memberRequest.role", target = "role"),
		@Mapping(source = "encodedPassword", target = "password")
	})
	Member toEntity(MemberRequest memberRequest, String encodedPassword);

	@Mappings({
		@Mapping(source = "request.userId", target = "userId"),
		@Mapping(source = "request.name", target = "name"),
		@Mapping(source = "request.email", target = "email"),
		@Mapping(source = "request.role", target = "role"),
		@Mapping(source = "request.phoneNumber", target = "phoneNumber"),
		@Mapping(source = "encodedPassword", target = "password")
	})
	Member toEntity(MemberRegistrationRequest request, String encodedPassword);

	MemberResponse toMemberResponse(Member member);

	DefaultUserDetails toUserDetails(Member member);
}
