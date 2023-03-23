package com.example.demomicroservice.model.dto.response.user;

import com.obys.common.validator.annotation.Gmail;
import com.obys.common.validator.annotation.Phone;
import com.obys.common.validator.annotation.Required;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
