package com.audistore.audi.repository;

import com.audistore.audi.model.BinhLuan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, Long> {

    @Query("SELECT b FROM BinhLuan b WHERE b.baiViet.id = :idBaiViet AND b.trangThai = 'hien_thi' ORDER BY b.ngayBinhLuan DESC")
    Page<BinhLuan> findByBaiVietIdAndTrangThaiHienThi(@Param("idBaiViet") Long idBaiViet, Pageable pageable);

    // Thêm query để lấy tất cả comments (cả root và replies)
    @Query("SELECT b FROM BinhLuan b WHERE b.baiViet.id = :idBaiViet AND b.trangThai = 'hien_thi' ORDER BY b.ngayBinhLuan DESC")
    List<BinhLuan> findAllByBaiVietIdAndTrangThaiHienThi(@Param("idBaiViet") Long idBaiViet);

    // Query để lấy replies của một comment
    @Query("SELECT b FROM BinhLuan b WHERE b.binhLuanCha.id = :idBinhLuanCha AND b.trangThai = 'hien_thi' ORDER BY b.ngayBinhLuan ASC")
    List<BinhLuan> findByBinhLuanChaIdAndTrangThaiHienThi(@Param("idBinhLuanCha") Long idBinhLuanCha);

    @Query("SELECT COUNT(b) FROM BinhLuan b WHERE b.baiViet.id = :idBaiViet AND b.trangThai = 'hien_thi'")
    Long countByBaiVietIdAndTrangThaiHienThi(@Param("idBaiViet") Long idBaiViet);

    @Query("SELECT b FROM BinhLuan b WHERE b.nguoiDung.id = :idNguoiDung AND b.trangThai = 'hien_thi' ORDER BY b.ngayBinhLuan DESC")
    Page<BinhLuan> findByNguoiDungId(@Param("idNguoiDung") Long idNguoiDung, Pageable pageable);

    @Query("SELECT b FROM BinhLuan b WHERE b.trangThai = :trangThai ORDER BY b.ngayBinhLuan DESC")
    Page<BinhLuan> findByTrangThai(@Param("trangThai") String trangThai, Pageable pageable);

    @Query("SELECT b FROM BinhLuan b WHERE b.baiViet.id = :idBaiViet")
    List<BinhLuan> findAllByBaiVietId(@Param("idBaiViet") Long idBaiViet);
} 