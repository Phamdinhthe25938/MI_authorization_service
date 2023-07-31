package com.example.demomicroservice.controller.comunicate;

import com.example.demomicroservice.service.AuthorService;
import com.the.common.model.payload.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/communicate/api-gateway")
public class ApiGatewayCommunicateController {


  @Resource
  private AuthorService authorService;

  @PostMapping("/validateToken")
  public ResponseEntity<BaseResponse<?>> validateToken(HttpServletRequest httpServletRequest) {
    return new ResponseEntity<>(authorService.validateToken(httpServletRequest), HttpStatus.OK);
  }
}
