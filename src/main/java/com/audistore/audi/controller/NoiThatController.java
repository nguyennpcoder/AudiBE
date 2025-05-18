package com.audistore.audi.controller;

import com.audistore.audi.dto.NoiThatDTO;
import com.audistore.audi.service.NoiThatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/noi-that")
public class NoiThatController {

    @Autowired
    private NoiThatService noiThatService;

    @GetMapping("/mau-xe/{mauXeId}")
    public ResponseEntity<List<NoiThatDTO>> getNoiThatByMauXeId(@PathVariable Long mauXeId) {
        return ResponseEntity.ok(noiThatService.getNoiThatByMauXeId(mauXeId));
    }

    @GetMapping("/mau-xe/{mauXeId}/default")
    public ResponseEntity<NoiThatDTO> getDefaultNoiThatByMauXeId(@PathVariable Long mauXeId) {
        return ResponseEntity.ok(noiThatService.getDefaultNoiThatByMauXeId(mauXeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoiThatDTO> getNoiThatById(@PathVariable Long id) {
        return ResponseEntity.ok(noiThatService.getNoiThatById(id));
    }

    @PostMapping
    public ResponseEntity<NoiThatDTO> createNoiThat(@RequestBody NoiThatDTO noiThatDTO) {
        return new ResponseEntity<>(noiThatService.createNoiThat(noiThatDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoiThatDTO> updateNoiThat(@PathVariable Long id, @RequestBody NoiThatDTO noiThatDTO) {
        return ResponseEntity.ok(noiThatService.updateNoiThat(id, noiThatDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoiThat(@PathVariable Long id) {
        noiThatService.deleteNoiThat(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mau-xe/{mauXeId}/add/{noiThatId}")
    public ResponseEntity<Void> addNoiThatToMauXe(
            @PathVariable Long mauXeId,
            @PathVariable Long noiThatId,
            @RequestParam(defaultValue = "false") Boolean laMacDinh) {
        noiThatService.addNoiThatToMauXe(mauXeId, noiThatId, laMacDinh);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mau-xe/{mauXeId}/default/{noiThatId}")
    public ResponseEntity<Void> setDefaultNoiThat(
            @PathVariable Long mauXeId,
            @PathVariable Long noiThatId) {
        noiThatService.setDefaultNoiThat(mauXeId, noiThatId);
        return ResponseEntity.ok().build();
    }
}