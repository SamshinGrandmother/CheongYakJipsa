package me.synn3r.jipsa.core.api.member.controller;

import java.util.List;
import me.synn3r.jipsa.core.api.base.domain.Request.Insert;
import me.synn3r.jipsa.core.api.base.domain.Request.Update;
import me.synn3r.jipsa.core.api.base.domain.Request.UpdatePassword;
import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import me.synn3r.jipsa.core.api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

  private final MemberService memberService;

  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  @GetMapping("/members")
  public List<MemberResponse> getMembers(MemberSearchCondition condition) {
    return memberService.findMembers(condition);
  }

  @GetMapping("/members/{id}")
  public MemberResponse getMember(@PathVariable long id) {
    return memberService.findMember(id);
  }

  @PostMapping("/members")
  public ResponseEntity<Long> saveMember(
    @Validated({Insert.class}) @RequestBody MemberRequest memberRequest) {
    return ResponseEntity.ok().body(memberService.saveMember(memberRequest));
  }

  @PutMapping("/members")
  public void updateMember(
    @Validated({Update.class}) @RequestBody MemberRequest memberRequest) {
    memberService.updateMember(memberRequest);
  }

  @PatchMapping("/members")
  public void updatePassword(
    @Validated({UpdatePassword.class}) @RequestBody MemberRequest memberRequest) {
    memberService.updatePassword(memberRequest);
  }

  @DeleteMapping("/members/{id}")
  public void deleteMember(@PathVariable long id) {
    memberService.deleteMember(id);
  }
}
