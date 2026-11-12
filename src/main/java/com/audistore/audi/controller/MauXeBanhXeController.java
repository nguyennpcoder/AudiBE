package com.audistore.audi.controller;

import com.audistore.audi.dto.BanhXeDTO;
import com.audistore.audi.service.MauXeBanhXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mau-xe/{idMau}/banh-xe")
@CrossOrigin(origins = "*")
public class MauXeBanhXeController {

    private final MauXeBanhXeService mauXeBanhXeService;

    @Autowired
    public MauXeBanhXeController(MauXeBanhXeService mauXeBanhXeService) {
        this.mauXeBanhXeService = mauXeBanhXeService;
    }

    @GetMapping
    public ResponseEntity<List<BanhXeDTO>> getBanhXeByMauXe(@PathVariable Long idMau) {
        try {
            List<BanhXeDTO> banhXeList = mauXeBanhXeService.getBanhXeByMauXe(idMau);
            return ResponseEntity.ok(banhXeList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/mac-dinh")
    public ResponseEntity<BanhXeDTO> getBanhXeMacDinhByMauXe(@PathVariable Long idMau) {
        try {
            BanhXeDTO banhXe = mauXeBanhXeService.getBanhXeMacDinhByMauXe(idMau);
            return ResponseEntity.ok(banhXe);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{idBanhXe}")
    public ResponseEntity<Void> addBanhXeToMauXe(@PathVariable Long idMau, 
                                                 @PathVariable Long idBanhXe,
                                                 @RequestParam(defaultValue = "false") Boolean laMacDinh) {
        try {
            mauXeBanhXeService.addBanhXeToMauXe(idMau, idBanhXe, laMacDinh);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idBanhXe}")
    public ResponseEntity<Void> removeBanhXeFromMauXe(@PathVariable Long idMau, 
                                                      @PathVariable Long idBanhXe) {
        try {
            mauXeBanhXeService.removeBanhXeFromMauXe(idMau, idBanhXe);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{idBanhXe}/mac-dinh")
    public ResponseEntity<Void> setBanhXeMacDinh(@PathVariable Long idMau, 
                                                 @PathVariable Long idBanhXe) {
        try {
            mauXeBanhXeService.setBanhXeMacDinh(idMau, idBanhXe);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 