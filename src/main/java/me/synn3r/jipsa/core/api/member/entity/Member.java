package me.synn3r.jipsa.core.api.member.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.api.base.entity.BaseEntity;
import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;
import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.component.security.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("사용자 및 관리자 테이블")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("사용자 및 관리자의 식별자")
	@Column(name = "member_id")
	private Long id;

	@NotBlank
	@Column(updatable = false)
	@Comment("사용자 및 관리자가 인증 시 사용하는 ID")
	private String userId;

	@NotBlank
	@Comment("사용자 및 관리자의 이름")
	private String name;

	@NotBlank
	@Email
	@Column
	@Comment("사용자 및 관리자의 이메일")
	private String email;

	@NotBlank
	@Comment("사용자 및 관리자의 패스워드")
	private String password;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Comment("사용자 및 관리자의 권한")
	private Role role;

	@Comment("사용자 및 관리자의 전화번호")
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Comment("사용자 및 관리자의 삭제 여부")
	private DeleteType deleteType = DeleteType.ACTIVE;

	@Builder
	public Member(Role role, String password, String email, String name, String userId) {
		this.role = role;
		this.password = password;
		this.email = email;
		this.name = name;
		this.userId = userId;
	}

	public void update(String name) {
		this.name = name;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void delete() {
		deleteType = DeleteType.DELETE;
	}

	public void updateMemberInfo(MemberRequest memberRequest) {
		this.email = memberRequest.getEmail();
		this.name = memberRequest.getName();
		this.phoneNumber = memberRequest.getPhoneNumber();
	}
}
