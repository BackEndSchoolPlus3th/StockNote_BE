package org.com.stocknote.global.kis;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KisTokenScheduler {
    private final KisKeyManager keyManager;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void refreshKeys() {
        keyManager.getAccessToken(); // Access Token 갱신
        keyManager.getWebSocketApprovalKey(); // WebSocket Approval Key 갱신
        System.out.println("Tokens and WebSocket keys refreshed.");
    }
}
