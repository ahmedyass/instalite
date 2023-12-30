package com.instalite.instalite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "http://vue-app:4200")
public class InstaliteApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstaliteApplication.class, args);
    }

}
