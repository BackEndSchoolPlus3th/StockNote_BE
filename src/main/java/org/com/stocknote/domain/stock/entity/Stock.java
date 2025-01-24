package org.com.stocknote.domain.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Stock {
    @Id
    private String code; //종목코드
    private String name; //종목명
    private String category; //종목분류
}
