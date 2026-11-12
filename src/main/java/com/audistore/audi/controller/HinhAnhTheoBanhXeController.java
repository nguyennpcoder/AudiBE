package com.audistore.audi.controller;

import com.audistore.audi.dto.HinhAnhTheoBanhXeDTO;
import com.audistore.audi.model.HinhAnhTheoBanhXe;
import com.audistore.audi.service.HinhAnhTheoBanhXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mau-xe/{idMau}/banh-xe/{idBanhXe}/hinh-anh")
@CrossOrigin(origins = "*")
public class HinhAnhTheoBanhXeController {
    
    private static final Logger logger = LoggerFactory.getLogger(HinhAnhTheoBanhXeController.class);
    private final HinhAnhTheoBanhXeService hinhAnhTheoBanhXeService;

    @Autowired
    public HinhAnhTheoBanhXeController(HinhAnhTheoBanhXeService hinhAnhTheoBanhXeService) {
        this.hinhAnhTheoBanhXeService = hinhAnhTheoBanhXeService;
    }

    @GetMapping
    public ResponseEntity<List<HinhAnhTheoBanhXeDTO>> getHinhAnhByMauXeAndBanhXe(@PathVariable Long idMau, 
                                                                               @PathVariable Long idBanhXe) {
        logger.info("GET request for wheel images - mau xe: {}, banh xe: {}", idMau, idBanhXe);
        try {
            List<HinhAnhTheoBanhXeDTO> hinhAnhList = hinhAnhTheoBanhXeService.getHinhAnhByMauXeAndBanhXe(idMau, idBanhXe);
            logger.info("Returning {} wheel images", hinhAnhList.size());
            return ResponseEntity.ok(hinhAnhList);
        } catch (Exception e) {
            logger.error("Error in getHinhAnhByMauXeAndBanhXe", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/mau-sac/{idMauSac}")
    public ResponseEntity<List<HinhAnhTheoBanhXeDTO>> getHinhAnhByMauXeAndBanhXeAndMauSac(@PathVariable Long idMau, 
                                                                                       @PathVariable Long idBanhXe,
                                                                                       @PathVariable Long idMauSac) {
        logger.info("GET request for wheel images with color - mau xe: {}, banh xe: {}, mau sac: {}", idMau, idBanhXe, idMauSac);
        try {
            List<HinhAnhTheoBanhXeDTO> hinhAnhList = hinhAnhTheoBanhXeService.getHinhAnhByMauXeAndBanhXeAndMauSac(idMau, idBanhXe, idMauSac);
            logger.info("Returning {} wheel images for specific color", hinhAnhList.size());
            return ResponseEntity.ok(hinhAnhList);
        } catch (Exception e) {
            logger.error("Error in getHinhAnhByMauXeAndBanhXeAndMauSac", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/loai-hinh/{loaiHinh}")
    public ResponseEntity<List<HinhAnhTheoBanhXeDTO>> getHinhAnhByMauXeAndBanhXeAndLoaiHinh(@PathVariable Long idMau, 
                                                                                          @PathVariable Long idBanhXe,
                                                                                          @PathVariable String loaiHinh) {
        logger.info("GET request for wheel images with type - mau xe: {}, banh xe: {}, loai hinh: {}", idMau, idBanhXe, loaiHinh);
        try {
            HinhAnhTheoBanhXe.LoaiHinh loaiHinhEnum = HinhAnhTheoBanhXe.LoaiHinh.valueOf(loaiHinh);
            List<HinhAnhTheoBanhXeDTO> hinhAnhList = hinhAnhTheoBanhXeService.getHinhAnhByMauXeAndBanhXeAndLoaiHinh(idMau, idBanhXe, loaiHinhEnum);
            logger.info("Returning {} wheel images for specific type", hinhAnhList.size());
            return ResponseEntity.ok(hinhAnhList);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid loai hinh: {}", loaiHinh, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error in getHinhAnhByMauXeAndBanhXeAndLoaiHinh", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<HinhAnhTheoBanhXe> createHinhAnh(@PathVariable Long idMau, 
                                                            @PathVariable Long idBanhXe,
                                                            @RequestBody HinhAnhTheoBanhXe hinhAnh) {
        logger.info("POST request to create wheel image for mau xe: {}, banh xe: {}", idMau, idBanhXe);
        try {
            HinhAnhTheoBanhXe savedHinhAnh = hinhAnhTheoBanhXeService.saveHinhAnh(hinhAnh);
            return ResponseEntity.ok(savedHinhAnh);
        } catch (Exception e) {
            logger.error("Error creating wheel image", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteHinhAnhByMauXeAndBanhXe(@PathVariable Long idMau, 
                                                               @PathVariable Long idBanhXe) {
        logger.info("DELETE request for wheel images - mau xe: {}, banh xe: {}", idMau, idBanhXe);
        try {
            hinhAnhTheoBanhXeService.deleteHinhAnhByMauXeAndBanhXe(idMau, idBanhXe);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting wheel images", e);
            return ResponseEntity.badRequest().build();
        }
    }
} 