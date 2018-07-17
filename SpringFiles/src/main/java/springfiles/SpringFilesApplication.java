package springfiles;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class SpringFilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringFilesApplication.class, args);
	}
	
	@Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("512KB");
        factory.setMaxRequestSize("512KB");
        return factory.createMultipartConfig();
    }
}
