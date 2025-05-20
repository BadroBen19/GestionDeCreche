package com.creche.creche.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.creche.creche.model.Role;
import com.creche.creche.model.User;
import com.creche.creche.repository.RoleRepository;
import com.creche.creche.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Create default roles if they don't exist
        if (roleRepository.count() == 0) {
            List<Role> defaultRoles = List.of(
                new Role("ROLE_ADMIN"),
                new Role("ROLE_EDUCATOR"),
                new Role("ROLE_KITCHEN"),
                new Role("ROLE_PARENT")
            );
            
            roleRepository.saveAll(defaultRoles);
            System.out.println("Default roles created");
        }
        
        // Create admin user if it doesn't exist
        if (!userRepository.existsByEmail("admin@creche.com")) {
            User adminUser = new User(
                "Admin",
                "User",
                "admin@creche.com",
                passwordEncoder.encode("admin123"), // Encode the password
                "1234567890"
            );
            
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
            
            adminUser.addRole(adminRole);
            userRepository.save(adminUser);
            System.out.println("Admin user created");
        }
    }
}