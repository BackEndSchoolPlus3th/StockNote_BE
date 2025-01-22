package org.com.stocknote.domain.stock;

public enum PeriodType {
    DAILY("D"),
    WEEKLY("W"),
    MONTHLY("M"),
    YEARLY("Y");

    private final String code;

    PeriodType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
