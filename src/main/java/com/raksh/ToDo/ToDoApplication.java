package com.raksh.ToDo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ToDoApplication {
	static{
		Dotenv dotenv = Dotenv.configure()
				.filename(".env")
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
	}
	public static void main(String[] args) {SpringApplication.run(ToDoApplication.class, args);
	}

}
