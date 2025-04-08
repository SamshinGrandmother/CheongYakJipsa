package me.synn3r.jipsa.core.api.member;

import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.component.security.Role;

public abstract class MemberTestSupport {


  protected Member getMockMember() {
    return getMockMember(1L, "abc123@gmail.com", "테스트123", null);
  }

  protected Member getMockMember(Long id, String email, String name, String password) {
    return new Member(id, email, name, password, Role.ADMIN);
  }

  protected MemberRequest getMockMemberRequest() {
    return getMockMemberRequest(null, "테스트123", "abc123@gmail.com", "test123!",
      "test123!");
  }

  protected MemberRequest getMockMemberRequest(Long id, String email, String name, String password,
    String passwordConfirm) {
    return new MemberRequest(id, email, name, Role.ADMIN, password, passwordConfirm);
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
