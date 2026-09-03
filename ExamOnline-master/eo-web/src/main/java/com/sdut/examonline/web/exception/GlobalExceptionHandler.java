package com.sdut.examonline.web.exception;

import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.web.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @InitBinder
    public void initWebBinder(WebDataBinder binder) {

    }

    @ModelAttribute
    public void addAttribute(Model model) {

    }

    @ExceptionHandler({ServiceException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResponseVo<String> serviceExceptionHandler(ServiceException e) {
        log.error(e.getMessage(), e);
        return ResponseVo.newResponse(e.getCode(), e.getMsg(), null);
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResponseVo<String> accessDeniedExceptionHandler(AccessDeniedException e) {
        log.error(e.getMessage(), e);
        return ResponseVo.newResponse(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMsg(), null);
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.OK)
    public ResponseVo<String> exceptionHandler(Exception e) {
        if (!(e instanceof HttpMediaTypeNotSupportedException)) {
            log.error(e.getMessage(), e);
        }
        return ResponseVo.newResponse(ErrorCode.INNER_ERROR.getCode(), ErrorCode.INNER_ERROR.getMsg(), null);
    }

}