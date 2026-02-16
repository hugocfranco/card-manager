package br.com.cardmanager.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    /**
     * Pointcut that targets all repositories, services, and REST controllers.
     * Adjust the package structure if necessary.
     */
    @Pointcut("execution(* br.com.cardmanager.controller..*(..)) || execution(* br.com.cardmanager.service..*(..))")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs when a method is entered and exited.
     */
    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        // Try to retrieve the HTTP Request (if we are in a Web Context)
        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
            }
        } catch (Exception e) {
            // Not a web request (e.g., background job or test), ignore.
        }

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // LOG INPUT (REQUEST)
        if (request != null) {
            log.info("Incoming Request: [{} {}] -> Class: {}.{}", 
                    request.getMethod(), request.getRequestURI(), className, methodName);
        } else {
            log.info("Executing Method: {}.{}", className, methodName);
        }
        
        // Log arguments (Ensure sensitive DTOs have safe toString() implementations)
        if (log.isDebugEnabled()) {
            log.debug("Arguments: {}", Arrays.toString(args));
        }

        Object result;
        try {
            // Proceed with method execution
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception in {}.{} with cause: {}", className, methodName, e.getMessage());
            throw e;
        }

        long executionTime = System.currentTimeMillis() - start;

        // LOG OUTPUT (RESPONSE)
        log.info("Method {}.{} finished in {}ms. Result: {}", 
                className, methodName, executionTime, result);

        return result;
    }
}