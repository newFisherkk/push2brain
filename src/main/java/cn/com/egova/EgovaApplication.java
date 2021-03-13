package cn.com.egova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EgovaApplication {
	static ConfigurableApplicationContext applicationContext = null;
	public static void main(String[] args) {
		SpringApplication.run(EgovaApplication.class, args);
	}
}
