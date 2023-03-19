package com.example.demomicroservice.repository;

import com.example.demomicroservice.model.entity.AppUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface IAppUserRepo extends CrudRepository<AppUser, Long> {
    AppUser findByUserName(String username);

    AppUser findAppUsersByPhoneNumber(String phone);
    AppUser findAppUsersByGmail(String gmail);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into app_user_roles value (:id, :role)")
    void saveRoleUser(@Param("id") Long id, @Param("role") int role);
}
