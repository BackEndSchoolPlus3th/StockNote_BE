package org.com.stocknote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableElasticsearchRepositories(basePackages = "org.com.stocknote.domain.searchDoc.repository")
@EnableJpaRepositories(basePackages = "org.com.stocknote.domain")
public class StockNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockNoteApplication.class, args);
    }
}
