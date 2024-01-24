package com.jorge.userservice;

import com.jorge.userservice.model.Role;
import com.jorge.userservice.model.User;
import com.jorge.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class UserServiceApplication implements CommandLineRunner {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Trying to Save User...");
		var jorgeUser = new User();
		jorgeUser.setUsername("Jorge");
		jorgeUser.setPassword(passwordEncoder.encode("jorge123"));

		var jorgeRole = Role.builder()
				.name("USER")
				.users(Set.of(jorgeUser))
				.build();

		jorgeUser.getRoles().add(jorgeRole);
		userRepository.save(jorgeUser);
		log.info("User Saved");
		// USER 1 -> username: "Jorge", roles: "USER"

		log.info("Trying to Save Admin User...");
		var adminUser = new User();
		adminUser.setUsername("Admin");
		adminUser.setPassword(passwordEncoder.encode("admin123"));

		var adminRole = Role.builder()
				.name("ADMIN")
				.users(Set.of(adminUser))
				.build();
		adminUser.getRoles().add(adminRole);

		userRepository.save(adminUser);
		log.info("Admin User Saved Successfully");

		// USER 2 -> username: "Admin", roles: "ADMIN"
	}
}
