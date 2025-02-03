package org.com.stocknote.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Getter
@ConfigurationProperties(prefix = "oauth2")
@Configuration
public class OAuth2Properties {
    private final Google google = new Google();
    private final Kakao kakao = new Kakao();

    @Getter
    public static class Google {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }

    @Getter
    public static class Kakao {
        private String clientId;
        private String redirectUri;
        private String clientName;
    }
}
