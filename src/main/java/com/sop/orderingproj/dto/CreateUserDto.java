package com.sop.orderingproj.dto;

import com.sop.orderingproj.enumeration.RoleName;

public record CreateUserDto(String username, String password, RoleName role) {

}
