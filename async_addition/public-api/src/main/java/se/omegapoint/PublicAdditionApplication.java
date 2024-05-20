package se.omegapoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class PublicAdditionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicAdditionApplication.class, args);
	}

}
