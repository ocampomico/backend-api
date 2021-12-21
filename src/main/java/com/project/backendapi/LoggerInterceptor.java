package com.project.backendapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoggerInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    private static final String START_TIME_MS = "startTimeMs";
    private static final String EXECUTE_TIME_MS = "executeTimeMs";
    private static final String PRINCIPAL_UUID = "principalUuid";
    private static final int MAX_BUFFER = 3072;
    private static final int MAX_DATA = 1024;


    private void logInfo(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder(MAX_BUFFER);
        sb.append("[");
        sb.append(new java.util.Date());
        sb.append("] - [");
        sb.append(request.getMethod(), 0, Math.min(request.getMethod().length(), MAX_DATA));
        sb.append("]");
        sb.append(request.getRequestURI(), 0,
                Math.min(request.getRequestURI().length(), MAX_DATA));

        if (request.getAttribute(PRINCIPAL_UUID) != null) {
            sb.append(" [USERID] - ");
            sb.append(request.getAttribute(PRINCIPAL_UUID));
        }

        sb.append(" [STARTMS] - ");
        sb.append(request.getAttribute(START_TIME_MS));

        if (request.getAttribute(EXECUTE_TIME_MS) != null) {
            sb.append(" [EXECUTEMS] - ");
            sb.append(request.getAttribute(EXECUTE_TIME_MS));
        }
        String log = sb.toString();

        logger.info(log);

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_MS, System.currentTimeMillis());
        if (request.getDispatcherType().name().equals("REQUEST")) {
            logInfo(request);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        long startTimeMs = (Long) request.getAttribute(START_TIME_MS);
        request.setAttribute(EXECUTE_TIME_MS, System.currentTimeMillis() - startTimeMs);
        logInfo(request);
    }
}
