package com.example.demomicroservice.model.dto.request.user;

import com.obys.common.validator.annotation.Gmail;
import com.obys.common.validator.annotation.Phone;
import com.obys.common.validator.annotation.Required;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRegistryRequest {

  @Length(max = 20)
  @Required(message = "{username.required}")
  private String userName;

  @Length(max = 20)
  @Required(message = "{password.required}")
  private String password;

  @Gmail(message = "{gmail.invalid}")
  @Required(message = "{gmail.required}")
  private String gmail;

  @Phone(message = "{phone.invalid}")
  @Required(message = "{phone.required}")
  private String phoneNumber;

}
