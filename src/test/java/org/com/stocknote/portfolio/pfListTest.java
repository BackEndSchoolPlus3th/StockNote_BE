package org.com.stocknote.portfolio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PortfolioTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("포트폴리오 목록 조회시 7개의 카테고리가 조회되어야 한다")
  void getPortfolioListTest() throws Exception {
    mockMvc.perform(get("/api/v1/portfolios"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(7));
  }
}
