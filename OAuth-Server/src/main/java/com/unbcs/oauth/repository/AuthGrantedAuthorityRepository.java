package com.unbcs.oauth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unbcs.oauth.model.AuthGrantedAuthority;

@Repository
public interface AuthGrantedAuthorityRepository extends JpaRepository<AuthGrantedAuthority, Long> {

	@Query(value = "select * from AuthGrantedAuthority where auth_user_detail_id = ?1", nativeQuery = true)
	List<AuthGrantedAuthority> findGrantedAuthorityByUserId(Long id);
}
