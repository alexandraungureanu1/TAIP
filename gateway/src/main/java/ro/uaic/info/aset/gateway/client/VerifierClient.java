package ro.uaic.info.aset.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ro.uaic.info.aset.gateway.dto.AgeVerifyDTO;
import ro.uaic.info.aset.gateway.dto.StudentVerifyDTO;

@FeignClient(name = "verifierClient", url = "localhost:8080")
public interface VerifierClient {

    @PostMapping("/verify/age")
    Boolean verifyAge(AgeVerifyDTO ageVerifyDTO);

    @PostMapping("/verify/student")
    Boolean verifyIsStudent(StudentVerifyDTO studentVerifyDTO);
}
