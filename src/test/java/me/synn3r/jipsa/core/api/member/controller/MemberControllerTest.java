package me.synn3r.jipsa.core.api.member.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import me.synn3r.jipsa.core.api.member.MemberTestSupport;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import me.synn3r.jipsa.core.api.member.entity.mapper.MemberMapper;
import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import me.synn3r.jipsa.core.api.member.service.MemberService;
import me.synn3r.jipsa.core.component.security.Role;
import me.synn3r.jipsa.core.config.security.TestSecurityConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(controllers = MemberController.class,
  includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TestSecurityConfig.class))
@Import(TestSecurityConfig.class)
@DisplayName("사용자 Rest API 테스트")
class MemberControllerTest extends MemberTestSupport {

  @MockBean
  private MemberService memberService;
  @MockBean
  private MemberRepository memberRepository;
  @MockBean
  private MemberMapper memberMapper;
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("사용자 리스트 검색 시 이메일 검색 조건 맵핑 테스트")
  void getMemberListSearchConditionEmailMappingTest() throws Exception {
    MemberSearchCondition mockMemberSearchCondition = getMockMemberSearchCondition("test123", null);
    mockMvc.perform(get("/members")
        .queryParam("email", mockMemberSearchCondition.getEmail())
        .queryParam("name", mockMemberSearchCondition.getName())
      )
      .andExpect(status().isOk());

    verify(memberService, atMost(1)).findMembers(assertArg(memberSearchCondition ->
      {
        assertEquals(mockMemberSearchCondition.getEmail(), memberSearchCondition.getEmail());
        assertEquals(mockMemberSearchCondition.getName(), memberSearchCondition.getName());
      }
    ));
  }

  @Test
  @DisplayName("사용자 리스트 검색 테스트")
  void getMemberListTest() throws Exception {
    mockMvc.perform(get("/members"))
      .andExpect(status().isOk());

    verify(memberService, atMost(1)).findMembers(isA(MemberSearchCondition.class));
  }

  @Test
  @DisplayName("사용자 상세 검색 테스트")
  void getMemberDetailTest() throws Exception {
    final long mockMemberId = 1L;
    mockMvc.perform(get("/members/{memberId}", mockMemberId))
      .andExpect(status().isOk());

    verify(memberService, atMost(1)).findMember(eq(mockMemberId));
  }


  @Test
  @DisplayName("사용자 추가 Request Body 맵핑 테스트")
  void saveMemberTest() throws Exception {

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("name", "테스트123");
    formData.add("userId", "abc123");
    formData.add("email", "abc123@gmail.com");
    formData.add("password", "Jipsa2025!");
    formData.add("passwordConfirm", "Jipsa2025!");
    formData.add("role", "ADMIN");
    formData.add("phoneNumber", "010-9109-8751");

    mockMvc.perform(post("/members")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .params(formData))
      .andExpect(status().isOk());

    verify(memberService, atMost(1))
      .saveMember(assertArg(memberRequest -> {
        assertEquals("테스트123", memberRequest.getName());
        assertEquals("abc123", memberRequest.getUserId());
        assertEquals("abc123@gmail.com", memberRequest.getEmail());
        assertEquals("Jipsa2025!", memberRequest.getPassword());
        assertEquals("Jipsa2025!", memberRequest.getPasswordConfirm());
        assertEquals(Role.ADMIN, memberRequest.getRole());
      }));
  }

  @Test
  @DisplayName("사용자 추가 시 이름 없으면 응답코드 400으로 반환")
  void saveMemberNameValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    jsonObject.put("role", Role.NORMAL.name());
    mockMvc
      .perform(post("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 추가 시 이메일 없으면 응답코드 400으로 반환")
  void saveMemberEmailValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("name", "테스트123");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    jsonObject.put("role", Role.NORMAL.name());
    mockMvc
      .perform(post("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 추가 시 이메일 포맷이 안맞으면 응답코드 400으로 반환")
  void saveMemberEmailFormatValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    jsonObject.put("role", Role.NORMAL.name());
    mockMvc
      .perform(post("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 추가 시 비밀번호 없으면 응답코드 400으로 반환")
  void saveMemberPasswordValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(post("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }


  @Test
  @DisplayName("사용자 추가 시 비밀번호 확인 없으면 응답코드 400으로 반환")
  void saveMemberPasswordConfirmValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("role", Role.NORMAL.name());
    mockMvc
      .perform(post("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 추가 시 비밀번호와 비밀번호 확인 값이 다를 시 메세지 반환")
  void saveMemberPasswordCheckConfirmValidationTest() throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put("id", 1L);
    map.put("password", "Jipsa2025!");
    map.put("passwordConfirm", "Jipsa2025!!");


    mockMvc
      .perform(patch("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(map)))
      .andExpect(status().isBadRequest());

  }

  @Test
  @DisplayName("사용자 추가 시 비밀번호 복잡도 검증")
  void saveMemberCheckPasswordComplexValidationTest() throws Exception {

    Map<String, Object> map = new HashMap<>();
    map.put("id", 1L);
    map.put("password", "test");
    map.put("passwordConfirm", "test");


    mockMvc
      .perform(patch("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(map)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(
        Matchers.containsString("비밀번호는 대/소문자, 숫자, 특수문자 포함 8글자 이상이어야 합니다.")));


  }


  @Test
  @DisplayName("사용자 변경 Request Body 맵핑 테스트")
  void updateMemberTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("id", 1L);
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("isEmailVerified", true);

    mockMvc
      .perform(put("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isOk());

    verify(memberService, atMost(1))
      .saveMember(assertArg(memberRequest -> {
        assertEquals(memberRequest.getId(), jsonObject.get("id"));
        assertEquals(memberRequest.getName(), jsonObject.get("name"));
        assertEquals(memberRequest.getEmail(), jsonObject.get("email"));
      }));
  }

  @Test
  @DisplayName("사용자 변경 시 Id 없으면 없으면 응답코드 400으로 반환")
  void updateMemberIdValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(put("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 변경 시 Id 값이 0일 때 응답코드 400으로 반환")
  void updateMemberIdZeroValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("id", 0L);
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(put("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 변경 시 Id 값이 음수일 때 응답코드 400으로 반환")
  void updateMemberIdMinusValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("id", -1L);
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(put("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest())
      .andDo(print());
  }

  @Test
  @DisplayName("사용자 변경 시 이름이 없으면 응답코드 400으로 반환")
  void updateMemberNameValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("id", 1L);
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(put("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 비밀번호 변경 Request Body 맵핑 테스트")
  void updateMemberPasswordTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("id", 1L);
    jsonObject.put("password", "Jipsa2025!");
    jsonObject.put("passwordConfirm", "Jipsa2025!");
    mockMvc
      .perform(patch("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isOk());

    verify(memberService, atMost(1))
      .saveMember(assertArg(memberRequest -> {
        assertEquals(memberRequest.getId(), jsonObject.get("id"));
        assertEquals(memberRequest.getPassword(), jsonObject.get("password"));
        assertEquals(memberRequest.getPasswordConfirm(), jsonObject.get("passwordConfirm"));
      }));
  }

  @Test
  @DisplayName("사용자 비밀번호 변경 시 Id 없으면 없으면 응답코드 400으로 반환")
  void updateMemberPasswordIdValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(patch("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 비밀번호 변경 시 Id 값이 0일 때 응답코드 400으로 반환")
  void updateMemberPasswordIdZeroValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("id", 0L);
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(patch("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 비밀번호 변경 시 Id 값이 음수일 때 응답코드 400으로 반환")
  void updateMemberPasswordIdMinusValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("id", -1L);
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(patch("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 비밀번호 변경 시 비밀번호 없으면 응답코드 400으로 반환")
  void updateMemberPasswordPasswordValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("passwordConfirm", "test123!");
    mockMvc
      .perform(post("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 비밀번호 변경 시 비밀번호 확인 없으면 응답코드 400으로 반환")
  void updateMemberPasswordPasswordConfirmValidationTest() throws Exception {
    Map<String, Object> jsonObject = new HashMap<>();
    jsonObject.put("name", "테스트123");
    jsonObject.put("email", "abc123@gmail.com");
    jsonObject.put("password", "test123!");
    mockMvc
      .perform(post("/members")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(jsonObject)))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("사용자 삭제 시 ID 맵핑 테스트")
  void deleteMemberTest() throws Exception {
    final long mockMemberId = 1L;
    mockMvc.perform(delete("/members/{memberId}", mockMemberId))
      .andExpect(status().isOk());

    verify(memberService, atMost(1)).deleteMember(eq(mockMemberId));
  }


}
