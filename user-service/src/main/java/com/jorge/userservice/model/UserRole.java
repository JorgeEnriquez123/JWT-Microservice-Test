package com.jorge.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jorge.userservice.model.pkclasses.UserRolePk;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "user_role")
@IdClass(UserRolePk.class)
public class UserRole {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonBackReference
    Role role;
}
