
package org.com.stocknote.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.oauth.service.CustomOAuth2UserService;
import org.com.stocknote.oauth.token.TokenAuthenticationFilter;
import org.com.stocknote.oauth.token.TokenExceptionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final SaveRequestFilter saveRequestFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/error",
                        "/swagger-ui/oauth2-redirect.html",
                        "/favicon.ico"
                );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(FrameOptionsConfig::disable).disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/",
                                "/oauth2/**",
                                "/auth/**",
                                "/auth/login",
                                "/api/volume",
                                "/api/v1/stockApis/filtered/*",
                                "/api/v1/stockApis/kospi",
                                "/api/v1/stockApis/kosdaq",
                                "/api/v1/stockApis/kospi200",
                                "/api/v1/stockApis/volume",
                                "/api/v1/stocks/*/vote/",
                                "/api/v1/stocks/*/vote-statistics",
                                "/api/v1/stocks/chart",
                                "/api/v1/stockApis/price",
                                "/api/v1/stocks/{stockCode}",
                                "/api/v1/stockApis/time-prices",
                                "/api/v1/stockApis/chart",
                                "/api/v1/votes/*",
                                "/api/v1/votes/{stockCode}/*",
                                "/auth/google/redirect",
                                "/auth/kakao/redirect",
                                "/auth/kakao/callback",
                                "/swagger-ui/**",
                                "/swagger-ui/oauth2-redirect.html",
                                "/v3/api-docs/**",
                                "/ws/**",
                                "/topic/**",
                                "/auth/google/manual",
                                "/auth/kakao/manual",
                                "/auth/google/token",
                                "/oauth2.googleapis.com/token",
                                "/api/v1/posts/*",
                                "/api/v1/posts/**",
                                "/api/v1/posts",
                                "/hashtag/search/*",
                                "/api/v1/posts/popular",
                                "sse/**",
                                "/api/v1/searchDocs/**",
                                "/public/performance/**",
                                "/api/v1/performance/**",
                                "/public/test-auth/**",
                                "/public/test/**"
                                ).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth ->
                        oauth.authorizationEndpoint(endpoint -> endpoint
                                        .baseUri("/oauth2/authorization")
                                )
                                .userInfoEndpoint(c -> c.userService(oAuth2UserService))
                                .successHandler(oAuth2SuccessHandler)
                                .redirectionEndpoint(e -> e
                                        .baseUri("/auth/{registrationId}/redirect")
                                )
                                .failureHandler((request, response, exception) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json;charset=UTF-8");
                                    Map<String, String> errorDetails = new HashMap<>();
                                    errorDetails.put("error", "Authentication failed");
                                    errorDetails.put("message", exception.getMessage());
                                    String errorJson = new ObjectMapper().writeValueAsString(errorDetails);
                                    response.getWriter().write(errorJson);
                                })
                )

                .addFilterBefore(saveRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass())

                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
    }
}
