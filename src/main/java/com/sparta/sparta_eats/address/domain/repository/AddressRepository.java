package com.sparta.sparta_eats.address.domain.repository;

import com.sparta.sparta_eats.address.domain.entity.Address;
import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findAllByUser(User user);
    boolean existsByUser(User user);
    Optional<Address> findByUserAndIsDefault(User user, boolean isDefault);
}
