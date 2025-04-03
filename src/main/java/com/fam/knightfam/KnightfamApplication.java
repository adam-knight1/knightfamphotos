package com.fam.knightfam;

import com.fam.knightfam.config.AwsSecretsConfig;
import com.fam.knightfam.config.S3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Map;

@EnableConfigurationProperties(S3Properties.class)
@SpringBootApplication
public class KnightfamApplication {

	public static void main(String[] args) {
		// Retrieve secrets from AWS Secrets Manager
		Map<String, String> secrets = AwsSecretsConfig();

		// Set the database username and password as system properties
		// so that Spring Boot picks them up during auto-configuration
		if (secrets != null) {
			System.setProperty("spring.datasource.username", secrets.get("DB_USERNAME"));
			System.setProperty("spring.datasource.password", secrets.get("DB_PASSWORD"));
		}



		SpringApplication.run(KnightfamApplication.class, args);
	}

}
