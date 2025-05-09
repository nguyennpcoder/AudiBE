package com.audistore.audi.aspect;

import com.audistore.audi.service.NhatKyHoatDongService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class ActivityLogAspect { // tự động log các hoạt động

    private final NhatKyHoatDongService nhatKyHoatDongService;

    @Autowired
    public ActivityLogAspect(NhatKyHoatDongService nhatKyHoatDongService) {
        this.nhatKyHoatDongService = nhatKyHoatDongService;
    }

    @Pointcut("@annotation(com.audistore.audi.annotation.LogActivity)")
    public void logActivityPointcut() {
    }

    @AfterReturning(pointcut = "logActivityPointcut()", returning = "result")
    public void logActivity(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            com.audistore.audi.annotation.LogActivity logActivity = method.getAnnotation(com.audistore.audi.annotation.LogActivity.class);
            String loaiHoatDong = logActivity.type();

            // Lấy thông tin request
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            // Lấy chi tiết hoạt động
            Map<String, Object> chiTietHoatDong = new HashMap<>();
            chiTietHoatDong.put("method", method.getName());
            chiTietHoatDong.put("class", method.getDeclaringClass().getSimpleName());
            
            // Thêm params nếu cấu hình log params là true
            if (logActivity.logParams()) {
                Object[] args = joinPoint.getArgs();
                String[] paramNames = signature.getParameterNames();
                
                for (int i = 0; i < args.length; i++) {
                    // Bỏ qua các params nhạy cảm như password
                    if (args[i] != null && 
                            !paramNames[i].toLowerCase().contains("password") && 
                            !paramNames[i].toLowerCase().contains("matkhau")) {
                        chiTietHoatDong.put(paramNames[i], args[i].toString());
                    }
                }
            }

            // Log hoạt động
            nhatKyHoatDongService.logHoatDong(loaiHoatDong, chiTietHoatDong, request);
        } catch (Exception e) {
            // Chỉ log lỗi, không ảnh hưởng đến luồng chính
            System.err.println("Lỗi khi log hoạt động: " + e.getMessage());
        }
    }
}