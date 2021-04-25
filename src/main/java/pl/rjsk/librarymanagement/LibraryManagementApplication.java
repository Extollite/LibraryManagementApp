package pl.rjsk.librarymanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

// Spring Security disabled because have to be setup
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }

    @PostConstruct
    public void postConstruct() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
