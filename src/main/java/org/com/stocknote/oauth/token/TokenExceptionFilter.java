package org.com.stocknote.oauth.token;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TokenExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            // 기존 예외 전달
            throw e;
        } catch (JwtException e) {
            // JWT 관련 예외 처리
            if (e instanceof ExpiredJwtException) {
                throw new CustomException(ErrorCode.TOKEN_EXPIRED);
            } else if (e instanceof MalformedJwtException) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
            throw new CustomException(ErrorCode.INVALID_JWT_SIGNATURE);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
