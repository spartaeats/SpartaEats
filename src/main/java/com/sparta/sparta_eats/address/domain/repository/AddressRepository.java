package com.sparta.sparta_eats.address.domain.repository;

import com.sparta.sparta_eats.address.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findAllByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Address> findByIsDefault(boolean isDefault);
}
