package me.synn3r.jipsa.core.api.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import me.synn3r.jipsa.core.api.base.domain.Request.Update;
import me.synn3r.jipsa.core.api.base.domain.Request.UpdatePassword;
import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import me.synn3r.jipsa.core.api.member.service.MemberService;

@RestController
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("/members")
	@Operation(summary = "사용자 전체 조회", description = "검색한 사용자를 조회할 수 있는 API")
	public List<MemberResponse> getMembers(MemberSearchCondition condition) {
		return memberService.findMembers(condition);
	}

	@GetMapping("/members/{id}")
	@Operation(summary = "사용자 조회", description = "한명의 사용자를 조회할 수 있는 API")
	public MemberResponse getMember(@PathVariable long id) {
		return memberService.findMember(id);
	}

	@PutMapping("/members")
	@Operation(summary = "마이 페이지 정보 업데이트", description = "마이 페이지에서 나의 정보 수정하는 API")
	public ResponseEntity<Void> updateMember(
		@Validated({Update.class}) @RequestBody MemberRequest memberRequest) {
		memberService.updateMember(memberRequest);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/members")
	public ResponseEntity<Void> updatePassword(
		@Validated({UpdatePassword.class}) @RequestBody MemberRequest memberRequest) {
		memberService.updatePassword(memberRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/members/{id}")
	public void deleteMember(@PathVariable long id) {
		memberService.deleteMember(id);
	}

}
