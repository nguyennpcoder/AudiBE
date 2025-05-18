package com.audistore.audi.service;

import com.audistore.audi.dto.NoiThatDTO;
import com.audistore.audi.model.MauXe;
import com.audistore.audi.model.MauXeNoiThat;
import com.audistore.audi.model.MauXeNoiThatId;
import com.audistore.audi.model.NoiThat;
import com.audistore.audi.repository.MauXeNoiThatRepository;
import com.audistore.audi.repository.MauXeRepository;
import com.audistore.audi.repository.NoiThatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoiThatService {

    @Autowired
    private NoiThatRepository noiThatRepository;

    @Autowired
    private MauXeRepository mauXeRepository;

    @Autowired
    private MauXeNoiThatRepository mauXeNoiThatRepository;

    public List<NoiThatDTO> getNoiThatByMauXeId(Long mauXeId) {
        Optional<MauXe> mauXeOpt = mauXeRepository.findById(mauXeId);

        if (!mauXeOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy mẫu xe với ID: " + mauXeId);
        }

        MauXe mauXe = mauXeOpt.get();
        List<MauXeNoiThat> mauXeNoiThats = mauXeNoiThatRepository.findByMauXe(mauXe);

        return mauXeNoiThats.stream()
                .map(mxnt -> {
                    NoiThatDTO dto = mapToDTO(mxnt.getNoiThat());
                    dto.setLaMacDinh(mxnt.getLaMacDinh());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public NoiThatDTO getDefaultNoiThatByMauXeId(Long mauXeId) {
        Optional<MauXeNoiThat> mauXeNoiThatOpt = mauXeNoiThatRepository.findDefaultByMauXeId(mauXeId);

        if (!mauXeNoiThatOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy nội thất mặc định cho mẫu xe ID: " + mauXeId);
        }

        NoiThatDTO dto = mapToDTO(mauXeNoiThatOpt.get().getNoiThat());
        dto.setLaMacDinh(true);
        return dto;
    }

    public NoiThatDTO getNoiThatById(Long id) {
        Optional<NoiThat> noiThatOpt = noiThatRepository.findById(id);

        if (!noiThatOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy nội thất với ID: " + id);
        }

        return mapToDTO(noiThatOpt.get());
    }

    @Transactional
    public NoiThatDTO createNoiThat(NoiThatDTO noiThatDTO) {
        NoiThat noiThat = mapToEntity(noiThatDTO);
        NoiThat savedNoiThat = noiThatRepository.save(noiThat);
        return mapToDTO(savedNoiThat);
    }

    @Transactional
    public NoiThatDTO updateNoiThat(Long id, NoiThatDTO noiThatDTO) {
        Optional<NoiThat> noiThatOpt = noiThatRepository.findById(id);

        if (!noiThatOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy nội thất với ID: " + id);
        }

        NoiThat noiThat = noiThatOpt.get();
        noiThat.setTen(noiThatDTO.getTen());
        noiThat.setMoTa(noiThatDTO.getMoTa());
        noiThat.setDuongDanAnh(noiThatDTO.getDuongDanAnh());
        noiThat.setGiaThem(noiThatDTO.getGiaThem());

        NoiThat updatedNoiThat = noiThatRepository.save(noiThat);
        return mapToDTO(updatedNoiThat);
    }

    @Transactional
    public void deleteNoiThat(Long id) {
        if (!noiThatRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nội thất với ID: " + id);
        }

        noiThatRepository.deleteById(id);
    }

    @Transactional
    public void addNoiThatToMauXe(Long mauXeId, Long noiThatId, Boolean laMacDinh) {
        Optional<MauXe> mauXeOpt = mauXeRepository.findById(mauXeId);
        Optional<NoiThat> noiThatOpt = noiThatRepository.findById(noiThatId);

        if (!mauXeOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy mẫu xe với ID: " + mauXeId);
        }

        if (!noiThatOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy nội thất với ID: " + noiThatId);
        }

        MauXe mauXe = mauXeOpt.get();
        NoiThat noiThat = noiThatOpt.get();

        // If this is set as default, unset any existing default
        if (laMacDinh) {
            Optional<MauXeNoiThat> existingDefault = mauXeNoiThatRepository.findByMauXeAndLaMacDinh(mauXe, true);
            existingDefault.ifPresent(mxnt -> {
                mxnt.setLaMacDinh(false);
                mauXeNoiThatRepository.save(mxnt);
            });
        }

        MauXeNoiThatId id = new MauXeNoiThatId(mauXeId, noiThatId);
        MauXeNoiThat mauXeNoiThat = new MauXeNoiThat();
        mauXeNoiThat.setId(id);
        mauXeNoiThat.setMauXe(mauXe);
        mauXeNoiThat.setNoiThat(noiThat);
        mauXeNoiThat.setLaMacDinh(laMacDinh);

        mauXeNoiThatRepository.save(mauXeNoiThat);
    }

    @Transactional
    public void setDefaultNoiThat(Long mauXeId, Long noiThatId) {
        this.addNoiThatToMauXe(mauXeId, noiThatId, true);
    }

    private NoiThatDTO mapToDTO(NoiThat noiThat) {
        NoiThatDTO dto = new NoiThatDTO();
        dto.setId(noiThat.getId());
        dto.setTen(noiThat.getTen());
        dto.setMoTa(noiThat.getMoTa());
        dto.setDuongDanAnh(noiThat.getDuongDanAnh());
        dto.setGiaThem(noiThat.getGiaThem());
        dto.setLaMacDinh(false); // Default value, will be overridden if needed
        return dto;
    }

    private NoiThat mapToEntity(NoiThatDTO dto) {
        NoiThat noiThat = new NoiThat();
        noiThat.setId(dto.getId());
        noiThat.setTen(dto.getTen());
        noiThat.setMoTa(dto.getMoTa());
        noiThat.setDuongDanAnh(dto.getDuongDanAnh());
        noiThat.setGiaThem(dto.getGiaThem());
        return noiThat;
    }
}