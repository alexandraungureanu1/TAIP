package ro.uaic.info.aset.dataprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class DataProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataProviderApplication.class, args);
	}

}
