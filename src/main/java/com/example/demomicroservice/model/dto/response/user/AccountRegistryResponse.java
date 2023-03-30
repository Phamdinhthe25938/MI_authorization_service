package com.example.demomicroservice.model.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccountRegistryResponse {

  private String userName;

  private String uuid;

  private String password;

  private String gmail;

  private String phoneNumber;

  private List<String> roles;
}
