package com.audistore.audi.service;

import com.audistore.audi.entity.ChatMessage;
import com.audistore.audi.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatMessageRepository repo;

    @Autowired
    public ChatService(ChatMessageRepository repo) {
        this.repo = repo;
    }

    public Page<ChatMessage> list(String room, int page, int size) {
        return repo.findByRoomOrderByIdDesc(room, PageRequest.of(page, size));
    }

    public ChatMessage send(ChatMessage msg) {
        return repo.save(msg);
    }

    public Optional<ChatMessage> findById(Long id) {
        return repo.findById(id);
    }

    public ChatMessage updateFlags(Long id, Boolean daXoa, Boolean recalled) {
        ChatMessage msg = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Message not found"));
        if (daXoa != null) {
            msg.setDaXoa(daXoa);
        }
        if (recalled != null) {
            msg.setRecalled(recalled);
        }
        return repo.save(msg);
    }
}


