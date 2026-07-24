package io.trail.incident;

import org.springframework.boot.SpringApplication;

public class TestTrailIncidentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TrailIncidentServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
