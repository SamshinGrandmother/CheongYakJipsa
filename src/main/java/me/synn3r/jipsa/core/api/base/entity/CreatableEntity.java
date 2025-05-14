package me.synn3r.jipsa.core.api.base.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class CreatableEntity {

  @CreatedDate
  @Column(updatable = false)
  @Comment("생성 시각")
  private LocalDateTime createdAt;
}
