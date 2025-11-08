package com.example.projectlxp.config.web;

import com.example.projectlxp.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleException(BusinessException e) {
        HttpStatus status = e.getHttpStatus();
        Map<String, Object> body = Map.of("errorMessage", e.getMessage());

        return ResponseEntity.status(status).body(body);
    }

    //아래부턴 대충 GPT 때린 advice 필요시 수정 (validation 의존성에서 나올 수 있는 것들)
    // === 1) @RequestBody DTO 검증 실패 ===
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpServletRequest req) {

        ProblemDetail pd = problem(400, "Validation failed",
            "요청 본문에 유효하지 않은 값이 있습니다.", req);

        List<Map<String, Object>> errors = new ArrayList<>(ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of(
                "field", fe.getField(),
                "code", fe.getCode(),
                "message", fe.getDefaultMessage(),
                "rejectedValue", fe.getRejectedValue()))
            .toList());

        // 글로벌 에러(object-level)도 포함
        errors.addAll(ex.getBindingResult().getGlobalErrors().stream()
            .map(ge -> Map.<String, Object>of(
                "field", null,
                "code", ge.getCode(),
                "message", ge.getDefaultMessage()))
            .toList());

        pd.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    // === 2) @ModelAttribute/바인딩 검증 실패 ===
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(400, "Validation failed",
            "요청 파라미터 바인딩/검증에 실패했습니다.", req);

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.<String, Object>of(
                "field", fe.getField(),
                "code", fe.getCode(),
                "message", fe.getDefaultMessage(),
                "rejectedValue", fe.getRejectedValue()))
            .toList();

        pd.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    // === 3) 파라미터 수준 제약 위반 ===
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {

        ProblemDetail pd = problem(400, "Validation failed",
            "요청 파라미터 제약 조건을 위반했습니다.", req);

        List<Map<String, Object>> errors = ex.getConstraintViolations().stream()
            .map(v -> Map.of(
                "field", v.getPropertyPath() == null ? null : v.getPropertyPath().toString(),
                "code", v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                "message", v.getMessage(),
                "rejectedValue", v.getInvalidValue()))
            .toList();

        pd.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    // === 4) JSON 파싱/형식 오류 ===
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleNotReadable(
        HttpMessageNotReadableException ex, HttpServletRequest req
    ) {
        String detail = "요청 본문을 읽을 수 없습니다. 형식/타입을 확인하세요.";
        // Jackson 원인 노출은 최소화(보안). 필요 시 특정 케이스만 친절 메시지 변환
        ProblemDetail pd = problem(400, "Invalid JSON", detail, req);
        return ResponseEntity.badRequest().body(pd);
    }

    // === 5) 단일 파라미터 타입 변환 실패 ===
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex, HttpServletRequest req
    ) {

        ProblemDetail pd = problem(400, "Type mismatch",
            String.format("파라미터 '%s' 값 '%s'를 %s 타입으로 변환할 수 없습니다.",
                ex.getName(), ex.getValue(), ex.getRequiredType() == null ? "요구된" : ex.getRequiredType().getSimpleName()),
            req);

        pd.setProperty("errors", List.of(Map.of(
            "field", ex.getName(),
            "code", "TypeMismatch",
            "message", "잘못된 타입",
            "rejectedValue", ex.getValue()
        )));
        return ResponseEntity.badRequest().body(pd);
    }

    // === 6) 필수 파라미터/경로 변수 누락(옵션) ===
    @ExceptionHandler({ MissingServletRequestParameterException.class, MissingPathVariableException.class })
    public ResponseEntity<ProblemDetail> handleMissing(Exception ex, HttpServletRequest req) {
        ProblemDetail pd = problem(400, "Missing parameter", ex.getMessage(), req);
        return ResponseEntity.badRequest().body(pd);
    }

    private ProblemDetail problem(int status, String title, String detail, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status), detail);
        pd.setTitle(title);
        pd.setType(URI.create("https://example.com/problems/validation-error"));
        pd.setProperty("timestamp", OffsetDateTime.now());
        pd.setProperty("instance", req.getRequestURI());
        // Observability 스택을 쓰면 traceId 연동
        pd.setProperty("traceId", MDC.get("traceId")); // or from Sleuth/Micrometer
        return pd;
    }
}
