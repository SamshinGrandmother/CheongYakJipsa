package me.synn3r.jipsa.core.api.member.controller;

import java.util.List;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessHistoryResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessSearchCondition;
import me.synn3r.jipsa.core.api.member.service.MemberAccessService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members/access")
public class MemberAccessHistoryController {

  private final MemberAccessService memberAccessService;

  public MemberAccessHistoryController(MemberAccessService memberAccessService) {
    this.memberAccessService = memberAccessService;
  }

  @GetMapping
  public List<MemberAccessHistoryResponse> getMemberAccessHistory(
    MemberAccessSearchCondition searchCondition) {
    return memberAccessService.getMemberAccessHistory(searchCondition);
  }


}
