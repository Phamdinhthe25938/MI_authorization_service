package com.example.demomicroservice.model.dto.request.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegistryEmployeeConsumer {

    private String account;

    private String email;

    private String telephone;
}
