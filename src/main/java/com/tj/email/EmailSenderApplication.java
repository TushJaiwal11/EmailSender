package com.tj.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tj.email.model.User;
import com.tj.email.model.domain.UserRole;
import com.tj.email.repository.UserRepository;

@SpringBootApplication(scanBasePackages = "com.tj.email")
@EnableScheduling
public class EmailSenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailSenderApplication.class, args);
	}
}

@Component
class AdminInitializer implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		String adminEmail = "admin@gmail.com";
		User existingUser = userRepository.findByEmail(adminEmail);

		if (existingUser == null) {
			User adminUser = new User();
			adminUser.setEmail(adminEmail);
			adminUser.setPassword(passwordEncoder.encode("123"));
			adminUser.setFullName("Tushar Jaiwal");
			adminUser.setPhone("1234567890");
			adminUser.setRole(UserRole.ROLE_ADMIN);
			adminUser.setRec_status(1L);
			adminUser.setReferralCode("REF12345");

			userRepository.save(adminUser);
			System.out.println("Admin user created successfully.");
		} else {
			System.out.println("Admin user already exists.");
		}
	}
}
