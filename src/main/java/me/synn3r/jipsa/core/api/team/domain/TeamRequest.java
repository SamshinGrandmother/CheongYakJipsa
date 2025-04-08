package me.synn3r.jipsa.core.api.team.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.api.base.domain.Request.Insert;
import me.synn3r.jipsa.core.api.base.domain.Request.Update;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequest {

  @NotNull(groups = Update.class)
  private Long id;
  @NotBlank(groups = {Insert.class, Update.class})
  private String name;

}
