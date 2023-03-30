package com.example.demomicroservice.controller;

import com.example.demomicroservice.model.dto.request.user.AccountLoginRequest;
import com.example.demomicroservice.model.dto.request.user.AccountRegistryRequest;
import com.example.demomicroservice.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
  @Resource
  AuthorService userService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody AccountLoginRequest request, BindingResult result) {
    return new ResponseEntity<>(userService.login(request, result), HttpStatus.OK);
  }

  @PostMapping("/registry")
  public ResponseEntity<?> hello(@Valid @RequestBody AccountRegistryRequest request, BindingResult result) {
    return new ResponseEntity<>(userService.registryUser(request, result), HttpStatus.OK);
  }
}
