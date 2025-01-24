package org.com.stocknote.domain.portfolio.portfolioStock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PfStockPatchRequest {
  @NotNull
  private int pfstockCount;

  @NotNull
  @Min(0)
  private int pfstockPrice;
}
