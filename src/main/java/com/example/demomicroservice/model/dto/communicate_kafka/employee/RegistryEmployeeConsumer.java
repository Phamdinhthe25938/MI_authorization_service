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
    private String account12;

    private String email123;

    private String telephone321;
}
