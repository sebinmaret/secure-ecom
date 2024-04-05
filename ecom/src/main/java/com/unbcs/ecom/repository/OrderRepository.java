package com.unbcs.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unbcs.ecom.model.ProductOrder;

@Repository
public interface OrderRepository extends JpaRepository<ProductOrder, String> {

	List<ProductOrder> findByUserId(String userId);

}
