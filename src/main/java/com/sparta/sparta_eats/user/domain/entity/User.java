package com.sparta.sparta_eats.user.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @Column(name = "user_id", length = 20)
  private String userId;

  @Column(nullable = false, length = 100)
  private String nickname;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(length = 13)
  private String phone;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  @Column(nullable = false)
  private Boolean isPublic = false;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "deleted_by", length = 100)
  private String deletedBy;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Builder
  public User(String userId, String nickname, String email, String phone,
      String password, UserRole role, Boolean isPublic) {
    this.userId = userId;
    this.nickname = nickname;
    this.email = email;
    this.phone = phone;
    this.password = password;
    this.role = role;
    this.isPublic = isPublic != null ? isPublic : false;
  }

  // 비즈니스 메서드
  public void updateLastLogin() {
    this.lastLoginAt = LocalDateTime.now();
  }

  public void changeRole(UserRole newRole) {
    this.role = newRole;
  }

  public void softDelete(String deletedBy) {
    this.deletedAt = LocalDateTime.now();
    this.deletedBy = deletedBy;
  }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }
}