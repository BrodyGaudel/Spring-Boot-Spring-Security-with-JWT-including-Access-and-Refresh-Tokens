package com.brody.app.api;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.brody.app.domain.Role;
import com.brody.app.domain.User;
import com.brody.app.service.UserService;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(path="/api")
public class UserController {
	
	private static final String APPLICATION_JSON_VALUE = "application/json";
	
	private UserService userService;

	public UserController(UserService userService) {
		
		this.userService = userService;
	}
	
	@GetMapping(path="/users")
	public ResponseEntity<List<User>> getUsers() {
		
		return ResponseEntity.ok().body(userService.getUsers());
		
	}
	
	@PostMapping(path="/user/save")
	public ResponseEntity<User> saveUser(@RequestBody User user) {
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
		
		return ResponseEntity.created(uri).body(userService.saveUser(user));
		
	}
	
	@PostMapping(path="/role/save")
	public ResponseEntity<Role> saveUser(@RequestBody Role role) {
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
		
		return ResponseEntity.created(uri).body(userService.saveRole(role));
		
	}
	
	@PostMapping(path="/role/addtouser")
	public ResponseEntity<User> addRoleToUser(@RequestBody RoleToUserForm form) {
		
		userService.addRoleToUser(form.getUsername(), form.getRoleName());
		
		return ResponseEntity.ok().build();
		
	}
	
	@GetMapping(path="/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
		
		String authorizationHeader = request.getHeader(AUTHORIZATION);
		if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
			
			try {
				
				String token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(token);
				String username = decodedJWT.getSubject();
				User user = userService.getUser(username);
				String access_token = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis()+10 *60 *1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);
				
				String refresh_token = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis()+30 *60 *1000))
						.withIssuer(request.getRequestURL().toString())
						.sign(algorithm);
				
				
				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);
				response.setContentType(APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
								
			} catch(Exception exception) {
				response.setHeader("error", exception.getMessage());
				response.setStatus(FORBIDDEN.value());
				//response.sendError(FORBIDDEN.value());
				
				Map<String, String> error = new HashMap<>();
				error.put("error_message", exception.getMessage());
				
				response.setContentType(APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}	
							
		} else {
			throw new RuntimeException("refresh token is missing");
		}
		
	}
	
}
class RoleToUserForm {
	private String username;
	private String roleName;
	public String getUsername() {
		return username;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
}
