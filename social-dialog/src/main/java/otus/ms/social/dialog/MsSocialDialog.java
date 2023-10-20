package otus.ms.social.dialog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsSocialDialog {

	public static void main(String[] args) {
		SpringApplication.run(MsSocialDialog.class, args);
	}

}
