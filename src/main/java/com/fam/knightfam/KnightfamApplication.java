package com.fam.knightfam;

import com.fam.knightfam.config.S3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(S3Properties.class)
@SpringBootApplication
public class KnightfamApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnightfamApplication.class, args);
	}

}
