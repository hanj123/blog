package com.hanj.blog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Aspect
@Component
public class LogAspect {


    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    //拦截web中所有类的所有方法
    @Pointcut("execution(* com.hanj.blog.web.*.*(..))")
    public void log(){

//        RequestLog requestLog =


        logger.info("我是拦截点");
    }

    @Before("execution(* com.hanj.blog.web.*.*(..))")
    public void doBefore(JoinPoint joinpoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();
        String classname = joinpoint.getTarget().getClass()+"."+joinpoint.getSignature().getName();
        Object[] args = joinpoint.getArgs();
        RequestLog requestLog = new RequestLog(url,ip,classname,args);
        logger.info("RequestLog:{}",requestLog);
    }


    @After("execution(* com.hanj.blog.web.*.*(..))")
    public void doAfter(){

//        logger.info("-------------doAfter------------");
    }


    @AfterReturning(returning = "result",pointcut = "log()")
    public void doAfterReturn(Object result){
         logger.info("return Result:{}",result);
    }


    private class RequestLog{
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args;

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "ReqeustLog{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}
