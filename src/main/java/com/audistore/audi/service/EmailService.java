package com.audistore.audi.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.audistore.audi.dto.DonHangDTO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender emailSender;
    
    public void guiEmailQuenMatKhau(String to, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@audi-vietnam.com");
        message.setTo(to);
        message.setSubject("Đặt lại mật khẩu - Audi Vietnam");
        
        String emailContent = String.format(
            "Xin chào,\n\n" +
            "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Audi của mình.\n\n" +
            "Mã đặt lại mật khẩu của bạn là: %s\n\n" +
            "Mã này có hiệu lực trong 15 phút.\n\n" +
            "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ Audi Vietnam",
            resetToken
        );
        
        message.setText(emailContent);
        emailSender.send(message);
    }
    
    // Thêm method sendEmail mới
    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@audi-vietnam.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);
    }

    public void guiEmailMatKhauMoi(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mật khẩu mới - Audi Store");
        message.setText(String.format(
            "Xin chào,\n\n" +
            "Tài khoản của bạn đã được mở khóa thành công.\n\n" +
            "Mật khẩu mới của bạn là: %s\n\n" +
            "Vui lòng đăng nhập và đổi mật khẩu ngay lập tức.\n\n" +
            "Trân trọng,\n" +
            "Audi Store Team",
            newPassword
        ));
        
        emailSender.send(message);
    }

    public void guiEmailXacNhanDatCoc(String to, DonHangDTO donHang, String transactionId) {
        if (to == null || to.isBlank()) return;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@audi-vietnam.com");
        message.setTo(to);
        message.setSubject("Xác nhận đặt cọc thành công - Audi Vietnam");

        StringBuilder sb = new StringBuilder();
        sb.append("Xin chào ").append(donHang.getTenNguoiDung() != null ? donHang.getTenNguoiDung() : "Quý khách").append(",\n\n");
        sb.append("Cảm ơn bạn đã tin tưởng lựa chọn Audi. Khoản đặt cọc của bạn đã được ghi nhận thành công.\n\n");
        sb.append("Thông tin đơn hàng:\n");
        sb.append("- Mã đơn hàng: ").append(donHang.getId()).append("\n");
        if (transactionId != null) sb.append("- Mã giao dịch: ").append(transactionId).append("\n");
        sb.append("- Số tiền đặt cọc: ").append(donHang.getTienDatCoc()).append(" VNĐ\n");
        sb.append("- Phương thức thanh toán: ").append(donHang.getPhuongThucThanhToan()).append("\n");
        if (donHang.getTenDaiLy() != null) sb.append("- Đại lý: ").append(donHang.getTenDaiLy()).append("\n");
        if (donHang.getGhiChu() != null) sb.append("- Ghi chú: ").append(donHang.getGhiChu()).append("\n");
        sb.append("\nTrong vòng 24h, chuyên viên bán hàng của Audi sẽ liên hệ để xác nhận và hướng dẫn các bước tiếp theo.\n");
        sb.append("Nếu có bất kỳ thắc mắc nào, vui lòng phản hồi email này hoặc liên hệ Tổng đài Audi: 1800-XXXX.\n\n");
        sb.append("Trân trọng,\nAudi Vietnam");

        message.setText(sb.toString());
        emailSender.send(message);
    }
}