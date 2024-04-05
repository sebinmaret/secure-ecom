package com.unbcs.ecom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unbcs.ecom.model.UserCart;

@Repository
public interface CartRepository extends JpaRepository<UserCart, String> {

	Optional<UserCart> findByUserId(String userId);

}
