package com.example.demomicroservice.service;


import com.example.demomicroservice.repository.IAppUserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("RoleService")
public class RoleService {

  @Resource
  private IAppUserRepo appUserRepo;

  @Transactional(rollbackFor = Exception.class)
  protected void saveRoleUser(Long id, int role) {
    appUserRepo.saveRoleUser(id, role);
  }
}
