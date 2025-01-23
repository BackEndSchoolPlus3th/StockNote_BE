package org.com.stocknote.domain.portfolio.portfolio.dto;

import jakarta.validation.Valid;
import lombok.Getter;

import java.util.Optional;

@Getter
public class PortfolioRequest {
  @Valid
  private String name;

  @Valid
  private String description;
}
