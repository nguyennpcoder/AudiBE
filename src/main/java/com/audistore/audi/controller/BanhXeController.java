package com.audistore.audi.controller;

import com.audistore.audi.dto.BanhXeDTO;
import com.audistore.audi.service.BanhXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banh-xe")
@CrossOrigin(origins = "*")
public class BanhXeController {

    private final BanhXeService banhXeService;

    @Autowired
    public BanhXeController(BanhXeService banhXeService) {
        this.banhXeService = banhXeService;
    }

    @GetMapping
    public ResponseEntity<List<BanhXeDTO>> getAllBanhXe() {
        List<BanhXeDTO> banhXeList = banhXeService.getAllBanhXe();
        return ResponseEntity.ok(banhXeList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BanhXeDTO> getBanhXeById(@PathVariable Long id) {
        try {
            BanhXeDTO banhXe = banhXeService.getBanhXeById(id);
            return ResponseEntity.ok(banhXe);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<BanhXeDTO>> searchBanhXe(@RequestParam String keyword) {
        List<BanhXeDTO> banhXeList = banhXeService.searchBanhXe(keyword);
        return ResponseEntity.ok(banhXeList);
    }

    @GetMapping("/kich-thuoc/{kichThuoc}")
    public ResponseEntity<List<BanhXeDTO>> getBanhXeByKichThuoc(@PathVariable String kichThuoc) {
        List<BanhXeDTO> banhXeList = banhXeService.getBanhXeByKichThuoc(kichThuoc);
        return ResponseEntity.ok(banhXeList);
    }

    @GetMapping("/chat-lieu/{chatLieu}")
    public ResponseEntity<List<BanhXeDTO>> getBanhXeByChatLieu(@PathVariable String chatLieu) {
        List<BanhXeDTO> banhXeList = banhXeService.getBanhXeByChatLieu(chatLieu);
        return ResponseEntity.ok(banhXeList);
    }

    @PostMapping
    public ResponseEntity<BanhXeDTO> createBanhXe(@RequestBody BanhXeDTO banhXeDTO) {
        try {
            BanhXeDTO createdBanhXe = banhXeService.createBanhXe(banhXeDTO);
            return ResponseEntity.ok(createdBanhXe);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BanhXeDTO> updateBanhXe(@PathVariable Long id, @RequestBody BanhXeDTO banhXeDTO) {
        try {
            BanhXeDTO updatedBanhXe = banhXeService.updateBanhXe(id, banhXeDTO);
            return ResponseEntity.ok(updatedBanhXe);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanhXe(@PathVariable Long id) {
        try {
            banhXeService.deleteBanhXe(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}