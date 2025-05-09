package com.audistore.audi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String vaiTro;
    
    public JwtResponse(String token, Long id, String email, String vaiTro) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.vaiTro = vaiTro;
    }
}