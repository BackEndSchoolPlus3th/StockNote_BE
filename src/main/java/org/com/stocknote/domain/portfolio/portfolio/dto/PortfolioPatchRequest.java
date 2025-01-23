package org.com.stocknote.domain.portfolio.portfolio.dto;


import lombok.Data;
import lombok.Getter;

import java.util.Optional;

@Data
public class PortfolioPatchRequest {
  private Long id;
  private Optional<String> name;
  private Optional<String> description;
}
