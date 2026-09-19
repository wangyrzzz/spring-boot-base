package com.example.demo.common;

import com.example.demo.enums.ResultCodeEnum;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 全局异常捕捉处理
     * 返回状态码:500
     * @param ex
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception ex) {
        return defHandler(ResultCodeEnum.ERROR.getCode(), ResultCodeEnum.ERROR.getMsg(), ex);
    }

    /**
     * 返回状态码:405
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return defHandler(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前请求方法", ex);
    }

    /**
     * 返回状态码:415
     * @param ex
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public Result handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        return defHandler(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "不支持当前媒体类型", ex);
    }


    /**
     * 自定义异常处理
     * 返回状态码: 500
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public Result handleException(ApiException ex, HttpServletResponse response) {
        if (ex.getCode() != null && ex.getCode() >= 400 && ex.getCode() < 600) {
            response.setStatus(ex.getCode());
        }
        return defHandler(ex.getCode(), ex.getMessage(), ex);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtException.class)
    public Result handleJwtException(JwtException ex) {
        return defHandler(HttpStatus.UNAUTHORIZED.value(), "认证令牌无效或已过期", ex);
    }


    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return defHandler(HttpStatus.OK.value(), "文件大小超出限制, 请压缩或降低文件质量!", ex);
    }


    /**
     * MethodArgumentNotValidException异常处理返回json （@Valid校验）
     * 返回状态码:400
     * @param ex
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result errorHandler(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        StringBuffer errorMsg = new StringBuffer();
        errors.stream().forEach(x -> {
            errorMsg.append(x.getDefaultMessage()).append(";");
        });
        return defHandler(ResultCodeEnum.FAIL.getCode(), errorMsg.toString(), ex);
    }


    /**
     * 全局处理参数校验异常（@Validated校验）
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Result validationBodyException(ConstraintViolationException e) {
        HashSet<ConstraintViolation<?>> set = (HashSet<ConstraintViolation<?>>) e.getConstraintViolations();
        final ArrayList<String> errorMsg = new ArrayList<>();
        set.forEach(c -> {
            errorMsg.add(c.getPropertyPath() + ": " + c.getMessageTemplate());
        });
        String message = "[" + String.join("; ", errorMsg) + "]";
        return defHandler(ResultCodeEnum.FAIL.getCode(), message, e);
    }


    /**
     * IllegalArgumentException异常处理返回json
     * 返回状态码:400
     * @param ex
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result errorHandler(IllegalArgumentException ex) {
        return defHandler(ResultCodeEnum.FAIL.getCode(), "参数解析失败", ex);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Result errorHandler(MissingServletRequestParameterException ex) {
        return defHandler(ResultCodeEnum.FAIL.getCode(), "缺少参数", ex);
    }

    /**
     * 返回状态码:400
     * @param ex
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public Result errorHandler(HttpMessageNotReadableException ex) {
        return defHandler(ResultCodeEnum.FAIL.getCode(), "字段类型错误", ex);
    }

    private Result defHandler(int code, String msg, Exception ex) {
        log.error(msg, ex);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.error("异常报告:-------------------------- {} --------------------------\n异常信息:{}\n异常类:{}\n异常方法:{}\n异常行数:{}",
                    format.format(new Date()),
                    ex.getMessage(),
                    ex.getStackTrace()[0].getClassName(),
                    ex.getStackTrace()[0].getMethodName(),
                    ex.getStackTrace()[0].getLineNumber()
            );
        } catch (Exception failEx) {
            log.error("捕获异常报告失败");
        }

        return Result.exception(code, msg);
    }


}
