package com.sephora.ecommerce;

import com.sephora.ecommerce.config.AppConstants;
import com.sephora.ecommerce.entities.Role;
import com.sephora.ecommerce.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ECommerceApplication implements CommandLineRunner {

	private RoleRepository roleRepository;

	public ECommerceApplication(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
	}

	@Override
	//@PostConstruct	//to ensure method run only once.
	public void run(String... args) throws Exception {
		try {
			Role adminRole = new Role();
			adminRole.setRoleId(AppConstants.ADMIN_ID);
			adminRole.setRoleName("ADMIN");

			Role userRole = new Role();
			userRole.setRoleId(AppConstants.USER_ID);
			userRole.setRoleName("USER");

			List<Role> roles = List.of(adminRole, userRole);

			List<Role> savedRoles = roleRepository.saveAll(roles);

			savedRoles.forEach(System.out::println);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
