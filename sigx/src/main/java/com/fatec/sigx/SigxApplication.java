package com.fatec.sigx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fatec.sigx.model.UserAppRepository;
import com.fatec.sigx.model.UsuarioApp;

@SpringBootApplication
public class SigxApplication {
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	UserAppRepository repository;
	public static void main(String[] args) {
		SpringApplication.run(SigxApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

	@Autowired
	public void inicializa() {
		UsuarioApp usuario = new UsuarioApp();
		usuario.setUserName("jose");
		usuario.setPassword(passwordEncoder.encode("123"));
		repository.save(usuario);
	}
	public BCryptPasswordEncoder pc() {
		return new BCryptPasswordEncoder();
	}
}
