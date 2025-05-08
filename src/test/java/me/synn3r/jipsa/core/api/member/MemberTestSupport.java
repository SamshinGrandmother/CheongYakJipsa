package me.synn3r.jipsa.core.api.member;

import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.component.security.Role;

public abstract class MemberTestSupport {


  protected Member getMockMember() {
    return getMockMember(1L, "abc123@gmail.com","abc123", "테스트123", null,"010-9109-8751");
  }

  protected Member getMockMember(Long id, String email, String userId, String name, String password,String phoneNumber) {
    return new Member(id, email, userId, name, password, Role.ADMIN,phoneNumber);
  }

  protected MemberRequest getMockMemberRequest() {
    return getMockMemberRequest(null,  "abc123@gmail.com","abc123", "테스트123", "test123!",
      "test123!","010-9109-8751");
  }

  protected MemberRequest getMockMemberRequest(Long id, String email, String userId, String name, String password,
    String passwordConfirm,String phoneNumber) {
    return new MemberRequest(id, email, userId, name, Role.ADMIN, password, passwordConfirm,phoneNumber);
  }

  protected MemberResponse getMockMemberResponse() {
    return getMockMemberResponse(1L, "abc123@gmail.com", "테스트123");
  }

  protected MemberResponse getMockMemberResponse(Long id, String email, String name) {
    MemberResponse mockMemberResponse = new MemberResponse();

    mockMemberResponse.setId(id);
    mockMemberResponse.setEmail(email);
    mockMemberResponse.setName(name);

    return mockMemberResponse;
  }

  protected MemberSearchCondition getMockMemberSearchCondition() {
    return getMockMemberSearchCondition("abc123@gmail.com", "테스트123");
  }

  protected MemberSearchCondition getMockMemberSearchCondition(String email, String name) {
    MemberSearchCondition mockMemberSearchCondition = new MemberSearchCondition();

    mockMemberSearchCondition.setEmail(email);
    mockMemberSearchCondition.setName(name);

    return mockMemberSearchCondition;
  }
}
