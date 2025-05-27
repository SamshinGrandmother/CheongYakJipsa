package me.synn3r.jipsa.core.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.batch.enumeration.HouseType;
import me.synn3r.jipsa.core.batch.enumeration.LocationType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class House {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "house_id")
  private long id;
  private long manageNumber;
  private LocalDate applyDateStart;
  private LocalDate applyDateEnd;
  private String name;
  @Enumerated(EnumType.STRING)
  private HouseType houseType;
  @Enumerated(EnumType.STRING)
  private LocationType locationType;
  private String address;
  private String url;
}
