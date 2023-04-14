package com.example.demomicroservice.controller;

import com.example.demomicroservice.model.dto.request.manager_personal.AccountManagerPersonalRegistryRequest;
import com.example.demomicroservice.model.dto.request.user.AccountLoginRequest;
import com.example.demomicroservice.model.dto.request.user.AccountUserRegistryRequest;
import com.example.demomicroservice.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthorController {
  @Resource
  AuthorService userService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody AccountLoginRequest request, BindingResult result) {
    return new ResponseEntity<>(userService.login(request, result), HttpStatus.OK);
  }

  @PostMapping("/registry/user")
  public ResponseEntity<?> registryUser(@Valid @RequestBody AccountUserRegistryRequest request, BindingResult result) {
    return new ResponseEntity<>(userService.registryUser(request, result), HttpStatus.OK);
  }

  @PostMapping("/registry/manager-personal")
  public ResponseEntity<?> registryManagerPersonal(@Valid @RequestBody AccountManagerPersonalRegistryRequest request,  BindingResult result) {
    return new ResponseEntity<>(userService.registryManagerPersonal(request, result), HttpStatus.OK);
  }
}
