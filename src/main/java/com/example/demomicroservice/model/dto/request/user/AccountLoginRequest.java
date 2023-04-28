package com.example.demomicroservice.model.dto.request.user;

import com.the.common.validator.annotation.Required;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountLoginRequest {

  @Required(message = "{username.required}")
  private String userName;

  @Required(message = "{password.required}")
  private String password;

}
