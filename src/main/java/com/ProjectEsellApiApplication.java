package com;
import com.services.*;
import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.services.FileStorageServices;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@EnableWebMvc
@SpringBootApplication
public class ProjectEsellApiApplication {

	@Resource
	FileStorageServices fileStorageServices;

	public static void main(String[] args) {
		SpringApplication.run(ProjectEsellApiApplication.class, args);
	}

	public void run(String... strings) throws Exception {

		fileStorageServices.init();
	}

	@Bean
	public Docket generateDoc() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com"))
				.build();
	}

}
