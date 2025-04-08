package me.synn3r.jipsa.core.api.team.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private DeleteType deleteType = DeleteType.ACTIVE;

  public void update(String name) {
    this.name = name;
  }

  public void delete() {
    this.deleteType = DeleteType.DELETE;
  }

  public Team(Long id, String name) {
    this.id = id;
    this.name = name;
  }
}
