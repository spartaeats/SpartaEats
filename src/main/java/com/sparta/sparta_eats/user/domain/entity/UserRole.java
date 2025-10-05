package com.sparta.sparta_eats.user.domain.entity;

public enum UserRole {
  CUSTOMER("ROLE_CUSTOMER"),
  OWNER("ROLE_OWNER"),
  MANAGER("ROLE_MANAGER"),
  MASTER("ROLE_MASTER");

  private final String authority;

  UserRole(String authority) {
    this.authority = authority;
  }

  public String getAuthority() {
    return this.authority;
  }
}