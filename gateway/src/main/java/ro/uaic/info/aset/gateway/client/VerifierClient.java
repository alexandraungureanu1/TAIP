package ro.uaic.info.aset.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ro.uaic.info.aset.gateway.dto.NationalityVerifyDTO;
import ro.uaic.info.aset.gateway.dto.StudentVerifyDTO;

//TODO wrap this calls with AOP for logging?? and maybe some other random stuff just for the sake of it
@FeignClient(name = "verifierClient", url = "localhost:8080")
public interface VerifierClient {

    @PostMapping("/verify/nationality")
    Boolean verifyNationality(NationalityVerifyDTO nationalityVerifyDTO);

    @PostMapping("/verify/student")
    Boolean verifyIsStudent(StudentVerifyDTO studentVerifyDTO);
}
