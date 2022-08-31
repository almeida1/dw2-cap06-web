package com.fatec.sigx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SigxApplication {

	public static void main(String[] args) {
		SpringApplication.run(SigxApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

}
