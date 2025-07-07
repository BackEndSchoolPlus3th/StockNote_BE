//package org.com.stocknote.global.faker;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class InitService {
//    private final DummyDataGenerator dummyDataGenerator;
//
//    @PostConstruct // 애플리케이션 시작 시 자동 실행
//    public void init() {
//        log.info("더미 데이터 생성 시작");
//        dummyDataGenerator.initializeDummyData();
//        log.info("더미 데이터 생성 완료");
//    }
//}
