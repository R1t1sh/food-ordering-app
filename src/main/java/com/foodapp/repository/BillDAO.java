package com.foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.foodapp.model.Bill;
import java.util.Optional;

@Repository
public interface BillDAO extends JpaRepository<Bill, Integer>{
    Optional<Bill> findByOrder_OrderId(Integer orderId);

}
