package verifier.verifiercomponent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class VerifierComponentApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerifierComponentApplication.class, args);
    }

}
