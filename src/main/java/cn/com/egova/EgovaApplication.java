package cn.com.egova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class EgovaApplication {
	static ConfigurableApplicationContext applicationContext = null;
	public static void main(String[] args) {
		SpringApplication.run(EgovaApplication.class, args);
	}
}
