package org.com.stocknote.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.error.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

//인가 실패 시 처리
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 인증 실패 시 반환할 상태 코드와 메시지 설정
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorCode errorCode = ErrorCode.SC_FORBIDDEN;
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now().toString(),errorCode.getHttpStatus().name(), "error happendfadskl");

        // 응답을 클라이언트에 전달
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

