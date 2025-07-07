package org.com.stocknote.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// src/main/java/org/com/stocknote/test/InfraTestController.java
@RestController
@RequestMapping("/api/test")
public class InfraTestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @GetMapping("/redis")
    public ResponseEntity<String> testRedis() {
        try {
            redisTemplate.opsForValue().set("test-key", "Hello Redis from StockNote!");
            String value = redisTemplate.opsForValue().get("test-key");
            return ResponseEntity.ok("✅ Redis 연결 성공: " + value);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Redis 연결 실패: " + e.getMessage());
        }
    }

    @GetMapping("/elasticsearch")
    public ResponseEntity<String> testElasticsearch() {
        try {
            IndexCoordinates indexCoordinates = IndexCoordinates.of("test-index");
            boolean exists = elasticsearchOperations.indexOps(indexCoordinates).exists();
            return ResponseEntity.ok("✅ ElasticSearch 연결 성공. 인덱스 존재: " + exists);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ ElasticSearch 연결 실패: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, String>> testAll() {
        Map<String, String> results = new HashMap<>();

        try {
            redisTemplate.opsForValue().set("test", "OK");
            results.put("Redis", "✅ 연결 성공");
        } catch (Exception e) {
            results.put("Redis", "❌ 연결 실패: " + e.getMessage());
        }

        try {
            IndexCoordinates indexCoordinates = IndexCoordinates.of("test");
            elasticsearchOperations.indexOps(indexCoordinates).exists();
            results.put("ElasticSearch", "✅ 연결 성공");
        } catch (Exception e) {
            results.put("ElasticSearch", "❌ 연결 실패: " + e.getMessage());
        }

        return ResponseEntity.ok(results);
    }
}