package me.synn3r.jipsa.core.api.member.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import me.synn3r.jipsa.core.api.member.MemberTestSupport;
import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.api.member.entity.mapper.MemberMapper;
import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 서비스 테스트")
class MemberServiceImplTest extends MemberTestSupport {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private MemberMapper memberMapper;
  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private MemberServiceImpl memberService;

  @Test
  @DisplayName("사용자 리스트 조회 테스트")
  void getMemberListTest() {
    MemberSearchCondition condition = new MemberSearchCondition();
    condition.setEmail("abc");
    MemberResponse mockMemberResponse1 = getMockMemberResponse();
    MemberResponse mockMemberResponse2 = getMockMemberResponse(2L, "def456@naver.com", "테스트456");
    List<MemberResponse> mockMemberResponseList = List.of(mockMemberResponse1, mockMemberResponse2);
    when(memberRepository.findMembers(condition))
      .thenReturn(mockMemberResponseList);

    List<MemberResponse> resultMemberList = memberService.findMembers(condition);

    assertEquals(mockMemberResponseList.size(), resultMemberList.size());

    for (int i = 0; i < resultMemberList.size(); i++) {
      MemberResponse actualMemberResponse = resultMemberList.get(i);
      MemberResponse expectedMemberResponse = mockMemberResponseList.get(i);

      assertEquals(actualMemberResponse.getId(), expectedMemberResponse.getId());
      assertEquals(actualMemberResponse.getEmail(), expectedMemberResponse.getEmail());
      assertEquals(actualMemberResponse.getName(), expectedMemberResponse.getName());
    }

    verify(memberRepository, atMost(1)).findMembers(condition);

  }

  @Test
  @DisplayName("사용자 상세 조회 테스트")
  void getMemberDetailTest() {
    Member mockMember = getMockMember();
    MemberResponse mockMemberResponse = getMockMemberResponse(mockMember.getId(),
      mockMember.getEmail(), mockMember.getName());
    when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
    when(memberMapper.toMemberResponse(mockMember)).thenReturn(mockMemberResponse);

    MemberResponse actualMockMemberResponse = memberService.findMember(mockMember.getId());

    assertEquals(mockMemberResponse.getId(), actualMockMemberResponse.getId());
    assertEquals(mockMemberResponse.getEmail(), actualMockMemberResponse.getEmail());
    assertEquals(mockMemberResponse.getName(), actualMockMemberResponse.getName());

    verify(memberRepository, atMost(1)).findById(mockMember.getId());
    verify(memberMapper, atMost(1)).toMemberResponse(mockMember);
  }

  @Test
  @DisplayName("없는 사용자를 상세조회 시 오류 예외발생해야함")
  void getMemberDetailNotFoundTest() {
    final long mockMemberId = 1L;
    when(memberRepository.findById(mockMemberId)).thenReturn(Optional.empty());

    NoSuchElementException exception = assertThrows(
      NoSuchElementException.class,
      () -> memberService.findMember(mockMemberId),
      "repository 에서 조회되지 않는 사용자를 상세조회 시 NoSuchElementException 이 발생해야합니다. "
    );

    assertEquals("사용자가 존재하지 않습니다. ", exception.getMessage());
  }

  @Test
  @DisplayName("사용자 추가 테스트")
  void saveMemberTest() {
    Member mockMember = getMockMember();
    MemberRequest memberRequest = getMockMemberRequest();
    when(memberMapper.toEntity(eq(memberRequest), anyString())).thenReturn(mockMember);
    when(memberRepository.existsMemberByEmail(memberRequest.getEmail()))
      .thenReturn(false);
    when(memberRepository.save(mockMember))
      .thenReturn(getMockMember());
    when(passwordEncoder.encode(anyString())).thenReturn("encodedString");

    long memberId = memberService.saveMember(memberRequest);

    assertEquals(mockMember.getId(), memberId);

    verify(passwordEncoder, atMost(1)).encode(memberRequest.getPassword());
    verify(memberMapper, atMost(1)).toEntity(memberRequest,
      passwordEncoder.encode(memberRequest.getPassword()));
    verify(memberRepository, atMost(1)).existsMemberByEmail(memberRequest.getEmail());

  }

  @Test
  @DisplayName("사용자 추가 시 기존 사용자와 이메일이 중복될 수 없음")
  void saveMemberDenyDuplicatedEmailTest() {
    MemberRequest memberRequest = getMockMemberRequest(null, "abc123@gmail.com","abc123", "테스트123",
      "test123!", "test123!", "010-9109-8751");
    when(memberRepository.existsMemberByEmail(memberRequest.getEmail()))
      .thenReturn(true);

    DuplicateKeyException exception = assertThrows(DuplicateKeyException.class,
      () -> memberService.saveMember(memberRequest),
      "기존 사용자의 이메일과 중복되면 IllegalArgumentException 이 발생해야 합니다. ");

    assertEquals("이미 존재하는 이메일 입니다. ", exception.getMessage());
  }

  @Test
  @DisplayName("사용자 변경 테스트")
  void updateMemberTest() {
    MemberRequest memberRequest = getMockMemberRequest(1L, "abc123@gmail.com","abc123", "테스트123", "test123!",
      "test123!", "010-9109-8751");
    Member mockMember = getMockMember();
    when(memberRepository.findById(memberRequest.getId())).thenReturn(Optional.of(mockMember));

    memberService.updateMember(memberRequest);

    verify(memberRepository, atMost(1)).existsMemberByEmail(memberRequest.getEmail());
  }

  @Test
  @DisplayName("변경할 사용자가 존재하지 않으면 예외가 발생해야함")
  void updateMemberNotFoundTest() {
    MemberRequest memberRequest = getMockMemberRequest(1L,  "abc123@gmail.com","abc123", "테스트123","test123!",
      "test123@", "010-9109-8751");
    when(memberRepository.findById(memberRequest.getId())).thenReturn(Optional.empty());

    NoSuchElementException exception = assertThrows(NoSuchElementException.class,
      () -> memberService.updateMember(memberRequest),
      "변경할 사용자가 없으면 NoSuchElementException 발생해야함");

    assertEquals("사용자가 존재하지 않습니다. ", exception.getMessage());
  }

  @Test
  @DisplayName("사용자 비밀번호 변경 테스트")
  void updateMemberPasswordTest() {
    MemberRequest memberRequest = getMockMemberRequest(1L, "abc123@gmail.com","abc123", "테스트123",
      "test123!", "test123!", "010-9109-8751");
    when(memberRepository.findById(memberRequest.getId()))
      .thenReturn(Optional.of(getMockMember()));

    memberService.updatePassword(memberRequest);

    verify(memberRepository, atMost(1)).findById(memberRequest.getId());
  }

  @Test
  @DisplayName("비밀번호 변경 할 사용자가 존재하지 않으면 예외가 발생해야함")
  void updatePasswordMemberNotFoundTest() {
    MemberRequest memberRequest = getMockMemberRequest(1L,  "abc123@gmail.com","abc123","테스트123", "test123!",
      "test123!", "010-9109-8751");
    when(memberRepository.findById(memberRequest.getId())).thenReturn(Optional.empty());

    NoSuchElementException exception = assertThrows(NoSuchElementException.class,
      () -> memberService.updatePassword(memberRequest),
      "비밀번호를 변경할 사용자가 없으면 NoSuchElementException 발생해야함");

    assertEquals("사용자가 존재하지 않습니다. ", exception.getMessage());
  }

  @Test
  @DisplayName("사용자 삭제 테스트")
  void deleteMemberTest() {
    final long mockMemberId = 1L;
    when(memberRepository.findById(mockMemberId)).thenReturn(Optional.of(getMockMember()));

    memberService.deleteMember(mockMemberId);

    verify(memberRepository, atMost(1)).findById(mockMemberId);
  }

  @Test
  @DisplayName("삭제할 사용자가 없으면 예외가 발생해야함")
  void deleteMemberNotFoundTest() {
    final long mockMemberId = 1L;
    when(memberRepository.findById(mockMemberId)).thenReturn(Optional.empty());

    NoSuchElementException exception = assertThrows(NoSuchElementException.class,
      () -> memberService.deleteMember(mockMemberId),
      "삭제할 사용자가 없으면 NoSuchElementException 발생해야함");

    assertEquals("사용자가 존재하지 않습니다. ", exception.getMessage());
  }

}
