package org.com.stocknote.domain.portfolio.note.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.note.dto.NoteResponse;
import org.com.stocknote.domain.portfolio.note.entity.Note;
import org.com.stocknote.domain.portfolio.note.service.NoteService;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.springframework.web.bind.annotation.*;
import org.com.stocknote.global.dto.GlobalResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
public class NoteController {
  private final NoteService noteService;
  private final PortfolioService portfolioService;

  @GetMapping("/{portfolio_no}/note")
  public GlobalResponse<List<NoteResponse>> getNote(@PathVariable("portfolio_no") Long portfolioNo) {
    List<Note> noteList = noteService.getNoteByPortfolioNo(portfolioNo);
    List<NoteResponse> response = noteList.stream().map(NoteResponse::from).collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @GetMapping("/allNote")
  public GlobalResponse<List<NoteResponse>> getNoteList() {
    List<Note> noteList = noteService.getNoteList();
    List<NoteResponse> response = noteList.stream().map(NoteResponse::from).collect(Collectors.toList());
    return GlobalResponse.success(response);
  }
}
