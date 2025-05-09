package com.audistore.audi.controller;

import com.audistore.audi.dto.*;
import com.audistore.audi.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tim-kiem")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/mau-xe")
    public ResponseEntity<SearchResultDTO<MauXeDTO>> searchMauXe(@RequestBody SearchCriteriaDTO criteria) {
        return ResponseEntity.ok(searchService.searchMauXe(criteria));
    }
    
    @PostMapping("/dai-ly")
    public ResponseEntity<SearchResultDTO<DaiLyDTO>> searchDaiLy(@RequestBody SearchCriteriaDTO criteria) {
        return ResponseEntity.ok(searchService.searchDaiLy(criteria));
    }
    
    @PostMapping("/ton-kho")
    public ResponseEntity<SearchResultDTO<TonKhoDTO>> searchTonKho(@RequestBody SearchCriteriaDTO criteria) {
        return ResponseEntity.ok(searchService.searchTonKho(criteria));
    }
}