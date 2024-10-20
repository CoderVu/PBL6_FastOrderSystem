package com.example.BE_PBL6_FastOrderSystem;

import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.example.BE_PBL6_FastOrderSystem.entity.Role;
@EnableScheduling
@SpringBootApplication
public class BePbl6FastOrderSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BePbl6FastOrderSystemApplication.class, args);
    }

    @Component
    @RequiredArgsConstructor
    class RoleInitialize implements ApplicationRunner{
        private final RoleRepository roleRepository;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            if (roleRepository.findByName(Role.name.ROLE_USER.name()).isEmpty()) {
                roleRepository.save(new Role(Role.name.ROLE_USER.name()));
            }
            if (roleRepository.findByName(Role.name.ROLE_ADMIN.name()).isEmpty()) {
                roleRepository.save(new Role(Role.name.ROLE_ADMIN.name()));
            }
            if (roleRepository.findByName(Role.name.ROLE_OWNER.name()).isEmpty()) {
                roleRepository.save(new Role(Role.name.ROLE_OWNER.name()));
            }
            if (roleRepository.findByName(Role.name.ROLE_SHIPPER.name()).isEmpty()) {
                roleRepository.save(new Role(Role.name.ROLE_SHIPPER.name()));
            }
        }
    }
}