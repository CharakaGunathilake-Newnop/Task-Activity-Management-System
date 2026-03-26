package edu.newnop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing // Enables @CreatedDate and @LastModifiedDate
@EntityScan(basePackages = "edu.newnop") // Scans all modules for JPA Entities
@EnableJpaRepositories(basePackages = "edu.newnop") // Scans all modules for Repositories
public class TaskManagerSystem {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerSystem.class,args);
    }
}