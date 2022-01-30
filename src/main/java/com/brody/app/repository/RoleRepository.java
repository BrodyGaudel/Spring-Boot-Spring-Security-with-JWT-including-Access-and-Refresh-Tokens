package com.brody.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brody.app.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Role findByName(String name);

}
