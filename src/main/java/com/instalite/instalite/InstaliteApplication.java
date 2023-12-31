package com.instalite.instalite;

import com.instalite.instalite.dto.RegisterDto;
import com.instalite.instalite.model.UserRole;
import com.instalite.instalite.repository.UserRepository;
import com.instalite.instalite.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "http://vue-app:4200")
public class InstaliteApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstaliteApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            UserService service,
            UserRepository repository
    ) {
        return args -> {
            repository.findByUsername("admin").ifPresentOrElse(
                action -> System.out.println("\"admin\" account already exists"),
                () -> createAdminAccount(service, repository)
            );
            System.out.println("\n\nAdmin password: password\n" +
                "Unless it has already been changed\n" +
                "If not, please change it immediately\n\n");
        };
    }

    private void createAdminAccount(UserService service, UserRepository repository) {
        var admin = RegisterDto.builder()
            .username("admin")
            .password("password")
            .email("admin@mail.com")
            .build();

        service.register(admin);

        repository.findByUsername("admin").ifPresent(user -> {
            user.setRole(UserRole.ADMINISTRATOR);
            repository.save(user);
        });
        System.out.println("Created \"admin\" account");
    }
}
