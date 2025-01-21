package org.com.stocknote.global.kis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
@RequiredArgsConstructor
public class KisService {

    private RestTemplate restTemplate;
    private final KisKeyManager kisKeyManager;

    //한국투자증권 OAuth Access Token 가져오기
    public String getAccessToken() {
        return kisKeyManager.getAccessToken();
    }

     //WebSocket 접속 키 가져오기
    public String getWebSocketApprovalKey() {
        return kisKeyManager.getWebSocketApprovalKey();
    }

}
