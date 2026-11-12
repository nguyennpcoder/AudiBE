package com.audistore.audi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String room; // "team" hoặc "dm:{minId}-{maxId}"

    @Column(name = "id_nguoi_gui", nullable = false)
    private Long senderId;

    @Column(name = "ten_nguoi_gui", length = 255)
    private String senderName;

    @Column(name = "avatar_nguoi_gui", length = 512)
    private String senderAvatar;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "recalled")
    private Boolean recalled = false;

    @Column(name = "da_xoa")
    private Boolean daXoa = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getRecalled() { return recalled; }
    public void setRecalled(Boolean recalled) { this.recalled = recalled; }

    public Boolean getDaXoa() { return daXoa; }
    public void setDaXoa(Boolean daXoa) { this.daXoa = daXoa; }
}


