package me.synn3r.jipsa.core.api.member.entity.mapper;

import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.component.security.DefaultUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface MemberMapper {

  @Mappings({
    @Mapping(source = "memberRequest.name", target = "name"),
    @Mapping(source = "memberRequest.email", target = "email"),
    @Mapping(source = "memberRequest.role", target = "role"),
    @Mapping(source = "encodedPassword", target = "password")
  })
  Member toEntity(MemberRequest memberRequest, String encodedPassword);


  MemberResponse toMemberResponse(Member member);

  DefaultUserDetails toUserDetails(Member member);
}
