package com.example.demomicroservice.model;

import lombok.Data;

@Data
public class CaptchaResponse {

  private boolean success = true;
  private String challenge_ts;
  private String hostname;
}
