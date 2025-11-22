package bg.softuni.myrabbitry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MyRabbitryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyRabbitryApplication.class, args);
	}

}
