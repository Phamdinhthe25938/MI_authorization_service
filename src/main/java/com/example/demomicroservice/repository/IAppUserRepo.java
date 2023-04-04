package com.example.demomicroservice.repository;

import com.example.demomicroservice.model.dto.mapper.GetRole;
import com.example.demomicroservice.model.entity.AppUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("IAppUserRepo")
public interface IAppUserRepo extends CrudRepository<AppUser, Long> {
  AppUser findByUserName(String username);

  AppUser findAppUsersByPhoneNumber(String phone);

  AppUser findAppUsersByGmail(String gmail);

  @Transactional
  @Modifying
  @Query(nativeQuery = true, value = "insert into app_user_roles value (:id, :role)")
  void saveRoleUser(@Param("id") Long id, @Param("role") int role);

  @Query(nativeQuery = true, value =
      "select au.user_name, r.role " +
          "from app_user as au " +
          "left join app_user_roles as aur " +
          "on au.id = aur.app_user_id " +
          "left join role as r " +
          "on aur.roles_id = r.id " +
          "where au.user_name  = :userName")
  List<GetRole> getRole(@Param("userName") String userName);

  @Modifying
  @Transactional
  @Query(nativeQuery = true, value = "update app_user set token = :token where user_name = :userName")
  void updateToken(@Param("token") String token, @Param("userName") String userName);
}
