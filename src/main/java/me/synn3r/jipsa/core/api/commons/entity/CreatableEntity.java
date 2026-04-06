package me.synn3r.jipsa.core.api.commons.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class CreatableEntity {

	@CreatedDate
	@Column(updatable = false)
	@Comment("생성 시각")
	private LocalDateTime createdAt;
}
