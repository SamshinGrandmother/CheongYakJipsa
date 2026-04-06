package me.synn3r.jipsa.core.api.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.synn3r.jipsa.core.api.commons.domain.Request.Insert;
import me.synn3r.jipsa.core.api.commons.domain.Request.Update;
import me.synn3r.jipsa.core.api.commons.domain.Request.UpdatePassword;
import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import me.synn3r.jipsa.core.api.member.service.MemberService;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;

@Tag(name = "Member", description = "회원 API")
@RestController
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("/members/me")
	@Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = MemberResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
	})
	public ResponseEntity<MemberResponse> getMyInfo(
		@AuthenticationPrincipal DefaultUserDetails userDetails) {
		MemberResponse member = memberService.findMemberByUserId(userDetails.getUsername());
		return ResponseEntity.ok(member);
	}

	@GetMapping("/members")
	@Secured("ADMIN")
	@Operation(summary = "회원 목록 조회", description = "검색 조건에 따라 회원 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
	})
	public List<MemberResponse> getMembers(
		@Parameter(description = "회원 검색 조건") MemberSearchCondition condition) {
		return memberService.findMembers(condition);
	}

	@GetMapping("/members/{id}")
	@Secured("ADMIN")
	@Operation(summary = "회원 단건 조회", description = "회원 ID로 특정 회원의 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = MemberResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content)
	})
	public MemberResponse getMember(
		@Parameter(description = "회원 ID", required = true) @PathVariable long id) {
		return memberService.findMember(id);
	}

	@PostMapping("/members")
	@Operation(summary = "회원가입", description = "신규 회원을 등록합니다. 이메일 인증이 완료되어야 합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원가입 성공, 생성된 회원 ID 반환"),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content)
	})
	public ResponseEntity<Long> saveMember(
		@Parameter(description = "회원가입 정보") @Validated({Insert.class}) MemberRequest memberRequest) {
		return ResponseEntity.ok().body(memberService.saveMember(memberRequest));
	}

	@PutMapping("/members")
	@Secured("ADMIN")
	@Operation(summary = "회원 정보 수정", description = "회원의 이름, 이메일, 전화번호 등의 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content)
	})
	public ResponseEntity<Void> updateMember(
		@Parameter(description = "수정할 회원 정보") @Validated({Update.class}) @RequestBody MemberRequest memberRequest) {
		memberService.updateMember(memberRequest);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/members")
	@Secured("ADMIN")
	@Operation(summary = "비밀번호 변경", description = "회원의 비밀번호를 변경합니다. 비밀번호 확인이 일치해야 합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
		@ApiResponse(responseCode = "400", description = "유효성 검증 실패 (비밀번호 불일치 등)", content = @Content),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content)
	})
	public ResponseEntity<Void> updatePassword(
		@Parameter(description = "비밀번호 변경 정보") @Validated({
			UpdatePassword.class}) @RequestBody MemberRequest memberRequest) {
		memberService.updatePassword(memberRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/members/{id}")
	@Secured("ADMIN")
	@Operation(summary = "회원 삭제", description = "회원 ID로 특정 회원을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "삭제 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content)
	})
	public void deleteMember(
		@Parameter(description = "삭제할 회원 ID", required = true) @PathVariable long id) {
		memberService.deleteMember(id);
	}

}
