package com.audistore.audi.service;

import com.audistore.audi.dto.BanhXeDTO;
import com.audistore.audi.model.*;
import com.audistore.audi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MauXeBanhXeService {

    private final MauXeBanhXeRepository mauXeBanhXeRepository;
    private final MauXeRepository mauXeRepository;
    private final BanhXeRepository banhXeRepository;
    private final HinhAnhTheoBanhXeRepository hinhAnhTheoBanhXeRepository;

    @Autowired
    public MauXeBanhXeService(MauXeBanhXeRepository mauXeBanhXeRepository,
                              MauXeRepository mauXeRepository,
                              BanhXeRepository banhXeRepository,
                              HinhAnhTheoBanhXeRepository hinhAnhTheoBanhXeRepository) {
        this.mauXeBanhXeRepository = mauXeBanhXeRepository;
        this.mauXeRepository = mauXeRepository;
        this.banhXeRepository = banhXeRepository;
        this.hinhAnhTheoBanhXeRepository = hinhAnhTheoBanhXeRepository;
    }

    public List<BanhXeDTO> getBanhXeByMauXe(Long idMau) {
        return mauXeBanhXeRepository.findByMauXeId(idMau).stream()
                .map(mxbx -> mapToDTO(mxbx.getBanhXe()))
                .collect(Collectors.toList());
    }

    public BanhXeDTO getBanhXeMacDinhByMauXe(Long idMau) {
        List<MauXeBanhXe> banhXeMacDinh = mauXeBanhXeRepository.findByMauXeIdAndLaMacDinh(idMau, true);
        if (banhXeMacDinh.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bánh xe mặc định cho mẫu xe với ID: " + idMau);
        }
        return mapToDTO(banhXeMacDinh.get(0).getBanhXe());
    }

    public void addBanhXeToMauXe(Long idMau, Long idBanhXe, Boolean laMacDinh) {
        MauXe mauXe = mauXeRepository.findById(idMau)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + idMau));
        
        BanhXe banhXe = banhXeRepository.findById(idBanhXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bánh xe với ID: " + idBanhXe));

        MauXeBanhXeId id = new MauXeBanhXeId(idMau, idBanhXe);
        MauXeBanhXe mauXeBanhXe = new MauXeBanhXe();
        mauXeBanhXe.setId(id);
        mauXeBanhXe.setMauXe(mauXe);
        mauXeBanhXe.setBanhXe(banhXe);
        mauXeBanhXe.setLaMacDinh(laMacDinh);

        mauXeBanhXeRepository.save(mauXeBanhXe);
    }

    public void removeBanhXeFromMauXe(Long idMau, Long idBanhXe) {
        MauXeBanhXeId id = new MauXeBanhXeId(idMau, idBanhXe);
        mauXeBanhXeRepository.deleteById(id);
    }

    public void setBanhXeMacDinh(Long idMau, Long idBanhXe) {
        // Reset tất cả bánh xe về không mặc định
        List<MauXeBanhXe> allBanhXe = mauXeBanhXeRepository.findByMauXeId(idMau);
        for (MauXeBanhXe mxbx : allBanhXe) {
            mxbx.setLaMacDinh(false);
            mauXeBanhXeRepository.save(mxbx);
        }

        // Set bánh xe được chọn làm mặc định
        MauXeBanhXeId id = new MauXeBanhXeId(idMau, idBanhXe);
        MauXeBanhXe mauXeBanhXe = mauXeBanhXeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy liên kết bánh xe với mẫu xe"));
        mauXeBanhXe.setLaMacDinh(true);
        mauXeBanhXeRepository.save(mauXeBanhXe);
    }

    private BanhXeDTO mapToDTO(BanhXe banhXe) {
        BanhXeDTO dto = new BanhXeDTO();
        dto.setId(banhXe.getId());
        dto.setTen(banhXe.getTen());
        dto.setMoTa(banhXe.getMoTa());
        dto.setKichThuoc(banhXe.getKichThuoc());
        dto.setChatLieu(banhXe.getChatLieu());
        dto.setGiaThem(banhXe.getGiaThem());
        dto.setDuongDanAnh(banhXe.getDuongDanAnh());
        return dto;
    }
} 