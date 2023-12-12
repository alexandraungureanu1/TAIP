package verifier.verifiercomponent.mop;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import verifier.verifiercomponent.dto.ocr.OcrRequestDTO;

@Slf4j
@Aspect
public class OCRServiceMonitoringAspect {
    private static final long THRESHOLD = 10;
    private static final Logger logger = LogManager.getLogger(OCRServiceMonitoringAspect.class);

    @Pointcut("execution(* verifier.verifiercomponent.service.api.CharacterRecognitionAPIService.performRequest(..)) " +
            "&& args(request)")
    public void executeRequest(OcrRequestDTO request) {
    }

    @Around("executeRequest(request)")
    public Object aroundOcrServiceCall(ProceedingJoinPoint joinPoint, OcrRequestDTO request) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        if (endTime - startTime > THRESHOLD) {
            log.info("Time above limit: " + (endTime - startTime));
        }

        return result;
    }
}
