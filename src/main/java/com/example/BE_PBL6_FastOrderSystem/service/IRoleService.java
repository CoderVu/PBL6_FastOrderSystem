package com.example.BE_PBL6_FastOrderSystem.service;
import com.example.BE_PBL6_FastOrderSystem.entity.Role;
import java.util.List;

public interface IRoleService {
    List<Role> getRoles();
    Role createRole(Role theRole);
}
