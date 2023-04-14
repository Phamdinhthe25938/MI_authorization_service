package com.example.demomicroservice.model.dto.request.manager_personal;

import com.obys.common.validator.annotation.Gmail;
import com.obys.common.validator.annotation.Phone;
import com.obys.common.validator.annotation.Required;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AccountManagerPersonalRegistryRequest {
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
  @Required(message = "{captcha.required}")
  private String captcha;
}
