package org.com.stocknote.global.kis;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class WebSocketClientService {

    private final KisKeyManager keyManager;

    public WebSocketClientService(KisKeyManager keyManager) {
        this.keyManager = keyManager;
    }

    public void connectToWebSocket() {
        try {
            // WebSocket URL 설정
            String url = "wss://openapi.koreainvestment.com:9443"; // 실전 WebSocket URL
            String approvalKey = keyManager.getWebSocketApprovalKey();

            // WebSocket 클라이언트 생성
            WebSocketClient client = new WebSocketClient(new URI(url)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("WebSocket connected.");

                    // WebSocket 연결 후 구독 메시지 전송 (예: 주식 종목 실시간 데이터 요청)
                    String subscribeMessage = "{\n" +
                            "  \"tr_id\": \"H0STCNT0\",\n" +
                            "  \"tr_key\": \"005930\"  \n}"; // 삼성전자 종목코드
                    this.send(subscribeMessage);
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Message received: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };

            // WebSocket 요청 헤더 추가
            client.addHeader("approval_key", approvalKey); // WebSocket 인증 키
            client.connect();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to WebSocket.");
        }
    }
}
