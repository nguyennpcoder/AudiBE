package com.audistore.audi.controller;

import com.audistore.audi.entity.ChatMessage;
import com.audistore.audi.service.ChatService;
import com.audistore.audi.service.NhatKyHoatDongService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@PreAuthorize("isAuthenticated()")
public class ChatController {

    private final ChatService chatService;
    private final NhatKyHoatDongService logService;

    @Autowired
    public ChatController(ChatService chatService, NhatKyHoatDongService logService) {
        this.chatService = chatService;
        this.logService = logService;
    }

    @GetMapping("/{room}")
    public ResponseEntity<Page<ChatMessage>> list(@PathVariable String room,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(chatService.list(room, page, size));
    }

    @PostMapping
    public ResponseEntity<ChatMessage> send(@RequestBody ChatMessage payload, HttpServletRequest request) {
        ChatMessage saved = chatService.send(payload);
        Map<String, Object> detail = new HashMap<>();
        detail.put("room", payload.getRoom());
        detail.put("senderId", payload.getSenderId());
        detail.put("len", payload.getContent() != null ? payload.getContent().length() : 0);
        logService.logHoatDong("chat_send", detail, request);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChatMessage> updateFlags(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Boolean daXoa = body.containsKey("da_xoa") ? parseBoolean(body.get("da_xoa")) : null;
        Boolean recalled = body.containsKey("recalled") ? parseBoolean(body.get("recalled")) : null;
        ChatMessage updated = chatService.updateFlags(id, daXoa, recalled);
        return ResponseEntity.ok(updated);
    }

    private Boolean parseBoolean(Object val) {
        if (val == null) return null;
        if (val instanceof Boolean b) return b;
        if (val instanceof Number n) return n.intValue() != 0;
        return Boolean.parseBoolean(String.valueOf(val));
    }
}


