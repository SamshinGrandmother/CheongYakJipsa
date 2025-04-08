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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MemberAccessHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private ResultType resultType;

  @ManyToOne
  @JoinColumn(name = "member_access_history_member_id", nullable = false, updatable = false)
  private Member member;

  @Enumerated(EnumType.STRING)
  private AuthenticationFailureType failureType;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime accessAt;

  public MemberAccessHistory(Member member, ResultType resultType,
    AuthenticationFailureType failureType) {
    this.member = member;
    this.resultType = resultType;
    this.failureType = failureType;
  }
}
