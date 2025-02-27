package com.unbcs.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unbcs.ecom.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

	List<Product> findAllByCategory(String category);
	
	

}
