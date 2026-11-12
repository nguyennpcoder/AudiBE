package com.audistore.audi.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {
    
    public String createSessionCookie(String email) {
        try {
            // Tạo session cookie cho Firebase Auth
            return FirebaseAuth.getInstance().createSessionCookie(email, null);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid email for session creation", e);
        } catch (Exception e) {
            throw new RuntimeException("Firebase not properly initialized", e);
        }
    }
    
    public boolean verifyOtp(String phoneNumber, String otp) {
        try {
            // TODO: Implement OTP verification logic with Firebase Phone Auth
            // Đây là placeholder - cần implement thực tế với Firebase Phone Auth
            System.out.println("Verifying OTP for phone: " + phoneNumber + ", OTP: " + otp);
            return true; // Placeholder return
        } catch (Exception e) {
            System.out.println("Error verifying OTP: " + e.getMessage());
            return false;
        }
    }
    
    public boolean isFirebaseAvailable() {
        try {
            return FirebaseAuth.getInstance() != null;
        } catch (Exception e) {
            return false;
        }
    }
} 