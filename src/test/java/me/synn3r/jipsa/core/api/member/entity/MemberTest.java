package me.synn3r.jipsa.core.api.member.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;
import me.synn3r.jipsa.core.api.member.MemberTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 엔티티 테스트")
class MemberTest extends MemberTestSupport {

  @Test
  @DisplayName("사용자 엔티티에 update() 메소드 수행 시 이름이 변경되어야 함")
  void updateNameMemberEntityTest() {
    Member mockMember = getMockMember();
    final String updateTargetName = "변경테스트";

    mockMember.update(updateTargetName);

    assertEquals(updateTargetName, mockMember.getName());
  }

  @Test
  @DisplayName("사용자 엔티티에 updatePassword() 메소드 수행 시 비밀번호가 변경되어야 함")
  void updatePasswordMemberEntityTest() {
    Member mockMember = getMockMember();
    final String mockPassword = "123456";

    mockMember.updatePassword(mockPassword);

    assertEquals(mockPassword, mockMember.getPassword());
  }

  @Test
  @DisplayName("사용자 엔티티에 delete() 메소드 수행 시 deleteType이 DELETE 로 변경되어야 함")
  void deleteMemberEntityTest() {
    Member mockMember = getMockMember();

    mockMember.delete();

    assertEquals(DeleteType.DELETE, mockMember.getDeleteType());
  }

}
