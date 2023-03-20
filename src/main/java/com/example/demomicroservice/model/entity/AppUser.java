package com.example.demomicroservice.model.entity;

import com.obys.common.model.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Unique
    private String uuid;
    @Unique
    private String userName;
    private String password;
    @Unique
    private String gmail;
    @Unique
    private String phoneNumber;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    public AppUser(@Unique String uuid, String userName, String password, String gmail, String phoneNumber) {
        this.uuid = uuid;
        this.userName = userName;
        this.password = password;
        this.gmail = gmail;
        this.phoneNumber = phoneNumber;
    }
}