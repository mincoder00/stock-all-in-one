package org.example.config;

import org.example.model.OauthInfo;
import org.example.model.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class AccessTokenManager {
    @Autowired
    private KisConfig kisConfig;
    private final WebClient webClient;
    public static String ACCESS_TOKEN;
    public static long last_auth_time = 0;
    private static final long TOKEN_EXPIRY_DURATION = 24 * 60 * 60 * 1000;

    public AccessTokenManager(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(KisConfig.REST_BASE_URL).build();
    }

    public String getAccessToken() {
        long currentTime = Instant.now().toEpochMilli();
        if (ACCESS_TOKEN == null || (currentTime - last_auth_time) > TOKEN_EXPIRY_DURATION) {
            ACCESS_TOKEN = generateAccessToken();
            last_auth_time = currentTime;
            System.out.println("generate ACCESS_TOKEN: " + ACCESS_TOKEN);
        }

        return ACCESS_TOKEN;
    }


    public String generateAccessToken() {
        String url = KisConfig.REST_BASE_URL + "/oauth2/tokenP";
        OauthInfo bodyOauthInfo = new OauthInfo();
        bodyOauthInfo.setGrant_type("client_credentials");
        bodyOauthInfo.setAppkey(kisConfig.getAPPKEY());
        bodyOauthInfo.setAppsecret(kisConfig.getAPPSECRET());

        Mono<TokenInfo> mono = webClient.post()
                .uri(url)
                .header("content-type", "application/json")
                .bodyValue(bodyOauthInfo)
                .retrieve()
                .bodyToMono(TokenInfo.class);

        TokenInfo tokenInfo = mono.block();
        if (tokenInfo == null) {
            throw new RuntimeException("액세스 토큰을 가져올 수 없습니다.");
        }

        ACCESS_TOKEN = tokenInfo.getAccess_token();

        return ACCESS_TOKEN;
    }
}
