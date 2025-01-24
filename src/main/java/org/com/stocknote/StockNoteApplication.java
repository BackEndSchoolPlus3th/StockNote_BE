package org.com.stocknote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StockNoteApplication {

    public static void main (String[] args) {
        SpringApplication.run(StockNoteApplication.class, args);
    }
}
