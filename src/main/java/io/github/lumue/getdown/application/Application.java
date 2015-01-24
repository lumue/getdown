package io.github.lumue.getdown.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@ComponentScan(basePackages = "io.github.lumue.getdown")
@EnableAutoConfiguration
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "${getdown.path.config}/*.properties}")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Bean
	public ExecutorService executorService() {
		return Executors.newCachedThreadPool();
	}
	

}
