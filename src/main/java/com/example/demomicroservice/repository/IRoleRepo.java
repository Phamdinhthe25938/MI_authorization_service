package com.example.demomicroservice.repository;

import com.example.demomicroservice.model.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface IRoleRepo extends CrudRepository<Role, Long> {
}
