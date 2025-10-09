package com.sparta.sparta_eats.address.domain.repository;

import com.sparta.sparta_eats.address.domain.entity.Address;
import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findAllByUser(User user);
    Optional<Address> findByUserAndIsDefault(User user, boolean isDefault);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user = :user AND a.isDefault = true")
    void unsetAllDefaultsByUser(@Param("user") User user);
}
