package com.example.backendproject.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect // 공통으로 관리하고 싶은 기능을 담당하는 클래스에 붙히는 어노테이션
public class LogAspect {

    // @Pointcut
    //AOP를 적용할 클래스
    @Pointcut("execution(* com.example.backendproject.board.service.BoardService..*(..)) " +
            "execution(* com.example.backendproject.board.controller..*(..)) ")
    public void method() {}

    // @Around 는 호출 시작과 종료 모두에 관여할 수 있는 AOP Advice
    @Around("method()")
    private Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();

        try {
            log.info("{AOP_LOG} {} 메서드 호출 시작 ", methodName);

            Object result = joinPoint.proceed(); // JoinPoint // AOP를 적용할 시점
            return result;
        }
        catch (Exception e) {
            log.error("[AOP_LOG] {} 메서드 예외 {} ", methodName, e.getMessage());
            return e;
        }
        finally {
            long end = System.currentTimeMillis();
            log.info("[AOP_LOG] {} 메서드 설정 완료 시작 ", methodName, end - start);
        }
    }

    // @Before aop가 실행되기 직전에 호출
    @Before("execution(* com.example.backendproject.board.service..*(..)) ")
    public void beforelog(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        log.info("[AOP_LOG][START] -> 메서드 = {} 호출 시작 ", method);
    }


    // @After aop가 실행되기 직전에 호출
    @After("execution(* com.example.backendproject.board.service..*(..)) ")
    public void afterlog(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        log.info("[AOP_LOG][END] -> 메서드 = {} 호출 종료 ", method);
    }

}
