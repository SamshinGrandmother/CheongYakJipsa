package me.synn3r.jipsa.core.api.member.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.api.base.entity.BaseEntity;
import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;
import me.synn3r.jipsa.core.component.security.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotBlank
  @Email
  @Column(updatable = false)
  private String email;
  @NotBlank
  private String name;
  @NotBlank
  private String password;
  @Enumerated(EnumType.STRING)
  @NotNull
  private Role role;
  @Enumerated(EnumType.STRING)
  private DeleteType deleteType = DeleteType.ACTIVE;

  public Member(Long id, String email, String name, String password, Role role) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.password = password;
    this.role = role;
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

}
