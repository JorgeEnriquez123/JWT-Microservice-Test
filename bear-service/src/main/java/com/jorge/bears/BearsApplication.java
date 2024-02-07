package com.jorge.bears;

import com.jorge.bears.model.Bear;
import com.jorge.bears.repository.BearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class BearsApplication implements CommandLineRunner {
	private final BearRepository bearRepository;

	public static void main(String[] args) {
		SpringApplication.run(BearsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		bearRepository.save(Bear.builder()
				.name("Shiro")
				.species("Polar Bear").build());
		bearRepository.save(Bear.builder()
				.name("Baloo")
				.species("Sloth bear").build());
		bearRepository.save(Bear.builder()
				.name("Kodiak")
				.species("Kodiak bear").build());
		bearRepository.save(Bear.builder()
				.name("Yogi")
				.species("Grizzly bear").build());
		bearRepository.save(Bear.builder()
				.name("Paddington")
				.species("Brown bear").build());
		bearRepository.save(Bear.builder()
				.name("Bo")
				.species("American black bear").build());
		bearRepository.save(Bear.builder()
				.name("Smokey")
				.species("Grizzly bear").build());
	}
}
