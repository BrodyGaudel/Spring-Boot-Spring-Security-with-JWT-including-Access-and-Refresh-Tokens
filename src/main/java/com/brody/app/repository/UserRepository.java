package com.brody.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brody.app.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByUsername(String username);
}
