package com.brody.app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class SpringSecurityJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtApplication.class, args);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*@Bean
	CommandLineRunner run(UserService userServive) {
		return args -> {
			userServive.saveRole(new Role("ROLE_USER"));
			userServive.saveRole(new Role("ROLE_MANAGER"));
			userServive.saveRole(new Role("ROLE_ADMIN"));
			userServive.saveRole(new Role("ROLE_SUPER_ADMIN"));
			
			userServive.saveUser(new User("Mia Kalifa","mia","1234",new ArrayList<>()));
			userServive.saveUser(new User("Jessy Volt","jessy","1234",new ArrayList<>()));
			userServive.saveUser(new User("Lala Ivey","lala","1234",new ArrayList<>()));
			userServive.saveUser(new User("Anna Polina","Anna","1234",new ArrayList<>()));
			
			userServive.addRoleToUser("jessy", "ROLE_USER");
			userServive.addRoleToUser("mia", "ROLE_MANAGER");
			userServive.addRoleToUser("mia", "ROLE_USER");
			userServive.addRoleToUser("lala", "ROLE_ADMIN");
			userServive.addRoleToUser("lala", "ROLE_USER");
			userServive.addRoleToUser("john", "ROLE_SUPER_ADMIN");
		
		};
	}*/
}
