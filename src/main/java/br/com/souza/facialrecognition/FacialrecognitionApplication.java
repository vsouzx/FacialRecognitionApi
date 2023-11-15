package br.com.souza.facialrecognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FacialrecognitionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacialrecognitionApplication.class, args);
		String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
		String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
	}

}
