package com.apps.miaowu.common.exception;

import com.apps.miaowu.bean.result.APIResult;
import com.apps.miaowu.bean.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Translates common request errors into clean APIResult JSON responses
 * instead of raw stack traces.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static APIResult<Object> badRequest(String message) {
        return APIResult.newResult(ResultCode.BadRequest, message, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResult<Object> handleMissingParam(MissingServletRequestParameterException e) {
        return badRequest("missing required parameter: " + e.getParameterName());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResult<Object> handleMissingPathVar(MissingPathVariableException e) {
        return badRequest("missing path variable: " + e.getVariableName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResult<Object> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String type = e.getRequiredType() == null ? "unknown" : e.getRequiredType().getSimpleName();
        return badRequest("invalid value for parameter '" + e.getName() + "', expected " + type);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResult<Object> handleBind(BindException e) {
        String field = e.getFieldError() == null ? "request" : e.getFieldError().getField();
        return badRequest("invalid value for parameter '" + field + "'");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResult<Object> handleNotReadable(HttpMessageNotReadableException e) {
        return badRequest("malformed request body");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResult<Object> handleIllegalArgument(IllegalArgumentException e) {
        return badRequest(e.getMessage() == null ? "illegal param" : e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public APIResult<Object> handleMethod(HttpRequestMethodNotSupportedException e) {
        return APIResult.newResult(405, "method " + e.getMethod() + " not supported", null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public APIResult<Object> handleOther(Exception e) {
        log.error("Unhandled exception", e);
        return APIResult.newResult(ResultCode.ServerInnerError, "internal server error", null);
    }
}
