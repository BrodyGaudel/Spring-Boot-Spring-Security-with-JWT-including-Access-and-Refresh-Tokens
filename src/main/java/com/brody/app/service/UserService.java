package com.brody.app.service;

import java.util.List;

import com.brody.app.domain.Role;
import com.brody.app.domain.User;

public interface UserService {

	User saveUser(User user);
	Role saveRole(Role role);
	void addRoleToUser(String username, String roleName);
	User getUser(String username);
	List<User> getUsers();
}
