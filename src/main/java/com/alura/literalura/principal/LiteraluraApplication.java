package com.alura.literalura.principal;

import com.alura.literalura.principal.Principal;
import com.alura.literalura.repository.LibrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.alura.literalura")
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private LibrosRepository repositorio;
	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repositorio);
		principal.mostrarMenu();
	}
}
