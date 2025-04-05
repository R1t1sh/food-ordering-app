package com.foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.foodapp.model.Customer;
import javax.transaction.Transactional;


@Repository
@Transactional
public interface CustomerDAO extends JpaRepository<Customer, Integer>{

}
