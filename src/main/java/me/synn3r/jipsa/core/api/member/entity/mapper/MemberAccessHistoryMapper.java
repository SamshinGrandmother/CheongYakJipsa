package me.synn3r.jipsa.core.api.member.entity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import me.synn3r.jipsa.core.api.commons.enumeration.ResultType;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.api.member.entity.MemberAccessHistory;
import me.synn3r.jipsa.core.global.component.security.enums.AuthenticationFailureType;

@Mapper
public interface MemberAccessHistoryMapper {

	@Mappings({
		@Mapping(source = "member", target = "member"),
		@Mapping(source = "resultType", target = "resultType")
	})
	MemberAccessHistory toEntity(Member member, ResultType resultType);

	@Mappings({
		@Mapping(source = "member", target = "member"),
		@Mapping(source = "resultType", target = "resultType"),
		@Mapping(source = "authenticationFailureType", target = "failureType")
	})
	MemberAccessHistory toEntity(Member member, ResultType resultType,
		AuthenticationFailureType authenticationFailureType);

	@Mappings({
		@Mapping(source = "memberId", target = "memberId"),
		@Mapping(source = "resultType", target = "resultType"),
		@Mapping(source = "authenticationFailureType", target = "failureType")
	})
	MemberAccessHistory toEntity(String memberId, ResultType resultType,
		AuthenticationFailureType authenticationFailureType);
}
