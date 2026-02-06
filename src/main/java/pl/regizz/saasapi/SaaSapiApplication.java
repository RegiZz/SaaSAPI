package pl.regizz.saasapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SaaSapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaaSapiApplication.class, args);
    }

}
