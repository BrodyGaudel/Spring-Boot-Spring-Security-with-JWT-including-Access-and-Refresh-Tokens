package com.brody.app.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brody.app.domain.Role;
import com.brody.app.domain.User;
import com.brody.app.repository.RoleRepository;
import com.brody.app.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;



@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
	
	private UserRepository userRepo;
	private RoleRepository roleRepo;
	private PasswordEncoder passwordEncoder;
	private static final Logger log = (Logger) LoggerFactory.getLogger(UserServiceImpl.class);
	
	public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
		
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepo.findByUsername(username);
		if(user==null) {
			throw new UsernameNotFoundException("User Not Found In The Database");
		}
		else {
			System.out.println("User Found In The Database");
		}
		
		//SimpleGrantedAuthority(role.getName())
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new  SimpleGrantedAuthority(role.getName()));
		});
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswoord(), authorities);
	}
	
	@Override
	public User saveUser(User user) {
		log.info("Saving new user {} in the database", user.getName());
		user.setPasswoord(passwordEncoder.encode(user.getPasswoord()));
		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		
		log.info("Saving new role {} in the database", role.getName());
		return roleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		
		log.info("Adding role {} to user {}", roleName,username);
		User user = userRepo.findByUsername(username);
		Role role = roleRepo.findByName(roleName);
		user.getRoles().add(role);
		
	}

	@Override
	public User getUser(String username) {
		
		log.info("Fetching user {}",username);
		return userRepo.findByUsername(username);
	}

	@Override
	public List<User> getUsers() {
		
		log.info("Fetching all users");
		return userRepo.findAll();
	}

	

}
