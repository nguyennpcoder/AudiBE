package com.audistore.audi.repository;

import com.audistore.audi.model.BaiViet;
import com.audistore.audi.model.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaiVietRepository extends JpaRepository<BaiViet, Long> {
    List<BaiViet> findByTacGia(NguoiDung tacGia);
    
    List<BaiViet> findByDanhMuc(BaiViet.DanhMuc danhMuc);
    
    List<BaiViet> findByDaXuatBan(Boolean daXuatBan);
    
    Page<BaiViet> findByDaXuatBan(Boolean daXuatBan, Pageable pageable);
    
    Page<BaiViet> findByDanhMucAndDaXuatBan(BaiViet.DanhMuc danhMuc, Boolean daXuatBan, Pageable pageable);
    
    @Query("SELECT b FROM BaiViet b WHERE b.tieuDe LIKE %:keyword% OR b.noiDung LIKE %:keyword%")
    List<BaiViet> searchBaiViet(String keyword);
    
    @Query("SELECT b FROM BaiViet b WHERE (b.tieuDe LIKE %:keyword% OR b.noiDung LIKE %:keyword%) AND b.daXuatBan = true")
    Page<BaiViet> searchBaiVietPublic(String keyword, Pageable pageable);
    
    @Query("SELECT b FROM BaiViet b WHERE b.theGan LIKE %:tag% AND b.daXuatBan = true")
    List<BaiViet> findByTag(String tag);
}