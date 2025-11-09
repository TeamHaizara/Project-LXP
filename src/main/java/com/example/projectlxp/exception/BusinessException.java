package com.example.projectlxp.exception;

import com.example.projectlxp.model.course.CourseStatus;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;
    private Throwable cause;

    @Deprecated(forRemoval = false)
    public BusinessException(ErrorCode exceptionCode) {
        this.message = exceptionCode.getMessage();
        this.httpStatus = exceptionCode.getStatus();
    }

    @Deprecated(forRemoval = false)
    public BusinessException(ErrorCode exceptionCode, Long id) {
        this.message = exceptionCode.getMessage() + id.toString();
        this.httpStatus = exceptionCode.getStatus();
    }

    @Deprecated(forRemoval = false)
    public BusinessException(ErrorCode exceptionCode, String field) {
        this.message = exceptionCode.getMessage() + field;
        this.httpStatus = exceptionCode.getStatus();
    }

    @Deprecated(forRemoval = false)
    private BusinessException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Deprecated(forRemoval = false)
    public BusinessException(ErrorCode exceptionCode, Long id1, Long id2) {
        this.message = String.format(exceptionCode.getMessage(), id1.toString(), id2.toString());
        this.httpStatus = exceptionCode.getStatus();
    }

    @Deprecated(forRemoval = false)
    public BusinessException(ErrorCode exceptionCode, String param1, String param2) {
        this.message = String.format(exceptionCode.getMessage(), param1, param2);
        this.httpStatus = exceptionCode.getStatus();
    }

    private BusinessException(Builder builder) {
        super(builder.getMessage(), builder.cause);
        this.httpStatus = builder.httpStatus;
        this.message = builder.getMessage();
        this.cause = builder.cause;
    }

    public interface BusinessExceptionBuilder {
        BusinessExceptionBuilder withId(Long... ids);

        BusinessExceptionBuilder withCourseStatus(CourseStatus... statuses);

        BusinessExceptionBuilder withField(String... fields);

        BusinessExceptionBuilder withCount(int count);

        BusinessExceptionBuilder withCause(Throwable cause);

        BusinessException build();
    }

    public static BusinessExceptionBuilder builder(ErrorCode errorCode) {
        return new Builder(errorCode);
    }

    private static class Builder implements BusinessExceptionBuilder {
        private final HttpStatus httpStatus;
        private final String messageTemplate;
        private final List<Object> params = new ArrayList<>();
        private Throwable cause;

        public Builder(ErrorCode errorCode) {
            this.httpStatus = errorCode.getStatus();
            this.messageTemplate = errorCode.getMessage();
        }

        public Builder withId(Long... ids) {
            this.params.addAll(Arrays.asList(ids));
            return this;
        }

        public Builder withCourseStatus(CourseStatus... statuses) {
            for (CourseStatus status : statuses) {
                this.params.add(status.name());
            }
            return this;
        }

        public Builder withField(String... fields) {
            this.params.addAll(Arrays.asList(fields));
            return this;
        }

        public Builder withCount(int count) {
            this.params.add(count);
            return this;
        }

        public Builder withCause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        private String getMessage() {
            if (params.isEmpty()) {
                return messageTemplate;
            }
            return String.format(messageTemplate, params.toArray());
        }

        public BusinessException build() {
            return new BusinessException(this);
        }
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
