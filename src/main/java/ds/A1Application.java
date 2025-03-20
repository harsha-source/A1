package ds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controllers", "services", "repositories", "models"})
public class A1Application {

	public static void main(String[] args) {

		SpringApplication.run(A1Application.class, args);
	}

}
