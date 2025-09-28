package com.www.viewpoint.global.exception;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorDetails {
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // Only include 'detail' if it's not null or empty
    private final Object detail;
}