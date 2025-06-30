package com.example.backendproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
//스프링에서 모든 컨르롤러의 예외를 한 곳에서 처리하기 위한 어노테이션
public class GlobalExceptionHandler {


    //컨트롤러에서 RuntimeException 에러가 발생했을 때 이 메서드가 대신 치리하도록 매핑
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {

        ErrorResponse errorResponse = new ErrorResponse(
                2000,
                "내가 전달하는 메세지",
                e.getMessage()
        );

        log.error(errorResponse.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    // 400: 파라미터 타입 오류, JSON 파싱 오류 등
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception e) {
        log.warn("[BAD_REQUEST] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다: " + e.getMessage());
    }


    // 400: DTO validation(@Valid) 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("[VALIDATION_FAIL] {}", e.getMessage());

        //유효성 검증 실패한 모든 필드 오류 리스트를 가져옴
        //유효성 검증 실패한 필드명과 이유를 콤마로 연결해서 한 줄 메시지로 만들어줌
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((m1, m2) -> m1 + ", " + m2)
                .orElse("유효성 검사 실패");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }

    //인증 실패  401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        log.warn("[LOGIN_FAIL] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    // 403: 인가(권한) 실패
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("[ACCESS_DENIED] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
    }

    //그 외 모든 예외 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("[EXCEPTION][UNHANDLED] ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
    }


    /**

     <<< 주요 HTTP 상태코드 >>>

     2xx: 성공
     코드    상수명                의미
     200    OK                    성공적으로 요청 처리
     201    CREATED                새 리소스 생성 성공
     202    ACCEPTED            요청이 접수되었으나, 처리 미완료
     204    NO_CONTENT            성공했지만 반환 데이터 없음


     3xx: 리다이렉트
     코드    상수명                의미
     301    MOVED_PERMANENTLY    영구 이동 (리소스 위치 바뀜)
     302    FOUND                임시 이동 (일시적 리다이렉트)
     304    NOT_MODIFIED        변경 없음 (캐시 활용)



     4xx: 클라이언트 오류 (클라이언트 잘못)
     코드    상수명                        의미
     400    BAD_REQUEST                    잘못된 요청, 파라미터/데이터 오류
     401    UNAUTHORIZED                인증 실패 (로그인 필요)
     403    FORBIDDEN                    권한 없음 (로그인했지만 권한부족)
     404    NOT_FOUND                    리소스 없음
     405    METHOD_NOT_ALLOWED            허용하지 않는 메서드
     409    CONFLICT                    요청 충돌 (예: 중복 데이터)
     415    UNSUPPORTED_MEDIA_TYPE        지원하지 않는 미디어 타입
     422    UNPROCESSABLE_ENTITY        처리할 수 없는 엔티티(유효성 불통과 등)
     429    TOO_MANY_REQUESTS            너무 많은 요청(요청 제한)


     5xx: 서버 오류 (서버 잘못)
     코드    상수명                        의미
     500    INTERNAL_SERVER_ERROR        서버 내부 에러
     501    NOT_IMPLEMENTED                아직 구현 안 됨
     502    BAD_GATEWAY                    게이트웨이/프록시 오류
     503    SERVICE_UNAVAILABLE            서비스 사용 불가(점검중, 과부하 등)
     504    GATEWAY_TIMEOUT                게이트웨이/프록시 시간 초과

     **/


}