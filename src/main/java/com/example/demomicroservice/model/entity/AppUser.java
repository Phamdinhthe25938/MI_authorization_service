package com.example.demomicroservice.model.entity;

import com.obys.common.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppUser extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @Unique
  @NotNull
  private String uuid;
  @Unique
  @NotNull
  private String userName;
  @NotNull
  private String password;
  @Unique
  @NotNull
  private String gmail;
  @Unique
  @NotNull
  private String phoneNumber;
  @NotNull
  private String token;
  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Role> roles;

  public AppUser(@Unique String uuid, String userName, String password, String gmail, String phoneNumber) {
    this.uuid = uuid;
    this.userName = userName;
    this.password = password;
    this.gmail = gmail;
    this.phoneNumber = phoneNumber;
  }
}