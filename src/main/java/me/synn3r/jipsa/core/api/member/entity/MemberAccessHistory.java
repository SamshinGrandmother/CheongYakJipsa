package me.synn3r.jipsa.core.api.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.api.base.enumeration.ResultType;
import me.synn3r.jipsa.core.component.security.enumerations.AuthenticationFailureType;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Immutable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Comment("사용자 및 관리자 접속 기록 테이블")
@Immutable
public class MemberAccessHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("사용자 및 관리자 접속 기록 식별자")
  @Column(name = "member_access_history_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Comment("사용자 및 관리자 접속 시도 결과")
  private ResultType resultType;

  @ManyToOne
  @JoinColumn(name = "member_access_history_member_id", nullable = false, updatable = false)
  @Comment("접속 시도한 사용자 및 관리자")
  private Member member;

  @Enumerated(EnumType.STRING)
  @Comment("접속 실패 시 실패 분류")
  private AuthenticationFailureType failureType;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  @Comment("접속 시도 시각")
  private LocalDateTime accessAt;

  public MemberAccessHistory(Member member, ResultType resultType,
    AuthenticationFailureType failureType) {
    this.member = member;
    this.resultType = resultType;
    this.failureType = failureType;
  }
}
