package com.foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.foodapp.model.Address;

@Repository
public interface AddressDAO extends JpaRepository<Address, Integer> {
}
