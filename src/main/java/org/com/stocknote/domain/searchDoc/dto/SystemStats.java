package org.com.stocknote.domain.searchDoc.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStats {
    private long totalPosts;
    private long totalComments;
    private long totalMembers;
    private LocalDateTime lastUpdated;

    // 데이터 규모 설명
    public String getDataScale() {
        if (totalPosts >= 50000) {
            return "LARGE";      // 5만개 이상
        } else if (totalPosts >= 10000) {
            return "MEDIUM";     // 1만 ~ 5만개
        } else if (totalPosts >= 1000) {
            return "SMALL";      // 1천 ~ 1만개
        } else {
            return "MINIMAL";    // 1천개 미만
        }
    }
}