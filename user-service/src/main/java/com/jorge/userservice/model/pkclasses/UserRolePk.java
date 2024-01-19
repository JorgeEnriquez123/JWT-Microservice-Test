package com.jorge.userservice.model.pkclasses;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRolePk implements Serializable {
    Long user;
    Long role;
}
