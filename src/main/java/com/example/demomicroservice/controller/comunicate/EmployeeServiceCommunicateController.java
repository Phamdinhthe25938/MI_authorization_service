package com.example.demomicroservice.controller.comunicate;

import com.example.demomicroservice.model.entity.AppUser;
import com.example.demomicroservice.repository.IAppUserRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/communicate/employee-service")
public class EmployeeServiceCommunicateController {


  @Resource
  private IAppUserRepo iAppUserRepo;

  @GetMapping("/get-all-account")
  List<String> getAllAccount() {
    return ((List<AppUser>) iAppUserRepo.findAll()).stream().map(AppUser::getUserName).collect(Collectors.toList());
  }

}
