package com.audistore.audi.service;

import com.audistore.audi.dto.HinhAnhTheoBanhXeDTO;
import com.audistore.audi.model.HinhAnhTheoBanhXe;
import com.audistore.audi.repository.HinhAnhTheoBanhXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HinhAnhTheoBanhXeService {
    
    private static final Logger logger = LoggerFactory.getLogger(HinhAnhTheoBanhXeService.class);
    private final HinhAnhTheoBanhXeRepository hinhAnhTheoBanhXeRepository;

    @Autowired
    public HinhAnhTheoBanhXeService(HinhAnhTheoBanhXeRepository hinhAnhTheoBanhXeRepository) {
        this.hinhAnhTheoBanhXeRepository = hinhAnhTheoBanhXeRepository;
    }

    public List<HinhAnhTheoBanhXeDTO> getHinhAnhByMauXeAndBanhXe(Long idMau, Long idBanhXe) {
        logger.info("Fetching wheel images for mau xe: {} and banh xe: {}", idMau, idBanhXe);
        try {
            List<HinhAnhTheoBanhXe> entities = hinhAnhTheoBanhXeRepository.findByMauXe_IdAndBanhXe_Id(idMau, idBanhXe);
            logger.info("Found {} wheel images", entities.size());
            return entities.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching wheel images for mau xe: {} and banh xe: {}", idMau, idBanhXe, e);
            throw new RuntimeException("Không thể lấy hình ảnh bánh xe", e);
        }
    }

    public List<HinhAnhTheoBanhXeDTO> getHinhAnhByMauXeAndBanhXeAndMauSac(Long idMau, Long idBanhXe, Long idMauSac) {
        logger.info("Fetching wheel images for mau xe: {}, banh xe: {} and mau sac: {}", idMau, idBanhXe, idMauSac);
        try {
            List<HinhAnhTheoBanhXe> entities = hinhAnhTheoBanhXeRepository.findByMauXe_IdAndBanhXe_IdAndMauSac_Id(idMau, idBanhXe, idMauSac);
            logger.info("Found {} wheel images for specific color", entities.size());
            return entities.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching wheel images for mau xe: {}, banh xe: {} and mau sac: {}", idMau, idBanhXe, idMauSac, e);
            throw new RuntimeException("Không thể lấy hình ảnh bánh xe theo màu sắc", e);
        }
    }

    public List<HinhAnhTheoBanhXeDTO> getHinhAnhByMauXeAndBanhXeAndLoaiHinh(Long idMau, Long idBanhXe, HinhAnhTheoBanhXe.LoaiHinh loaiHinh) {
        logger.info("Fetching wheel images for mau xe: {}, banh xe: {} and loai hinh: {}", idMau, idBanhXe, loaiHinh);
        try {
            List<HinhAnhTheoBanhXe> entities = hinhAnhTheoBanhXeRepository.findByMauXe_IdAndBanhXe_IdAndLoaiHinh(idMau, idBanhXe, loaiHinh);
            logger.info("Found {} wheel images for specific type", entities.size());
            return entities.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching wheel images for mau xe: {}, banh xe: {} and loai hinh: {}", idMau, idBanhXe, loaiHinh, e);
            throw new RuntimeException("Không thể lấy hình ảnh bánh xe theo loại hình", e);
        }
    }

    public void deleteHinhAnhByMauXeAndBanhXe(Long idMau, Long idBanhXe) {
        logger.info("Deleting wheel images for mau xe: {} and banh xe: {}", idMau, idBanhXe);
        hinhAnhTheoBanhXeRepository.deleteByMauXe_IdAndBanhXe_Id(idMau, idBanhXe);
    }

    public HinhAnhTheoBanhXe saveHinhAnh(HinhAnhTheoBanhXe hinhAnh) {
        logger.info("Saving wheel image: {}", hinhAnh.getDuongDanAnh());
        return hinhAnhTheoBanhXeRepository.save(hinhAnh);
    }

    private HinhAnhTheoBanhXeDTO mapToDTO(HinhAnhTheoBanhXe entity) {
        HinhAnhTheoBanhXeDTO dto = new HinhAnhTheoBanhXeDTO();
        dto.setId(entity.getId());
        dto.setIdMau(entity.getMauXe().getId());
        dto.setIdBanhXe(entity.getBanhXe().getId());
        dto.setIdMauSac(entity.getMauSac() != null ? entity.getMauSac().getId() : null);
        dto.setDuongDanAnh(entity.getDuongDanAnh());
        dto.setLoaiHinh(entity.getLoaiHinh().name());
        dto.setViTri(entity.getViTri());
        return dto;
    }
} 