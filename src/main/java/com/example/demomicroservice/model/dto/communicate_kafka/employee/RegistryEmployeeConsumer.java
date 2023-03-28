package com.example.demomicroservice.model.dto.communicate_kafka.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistryEmployeeConsumer {
    private String account;

    private String email;

    private String telephone;
}
