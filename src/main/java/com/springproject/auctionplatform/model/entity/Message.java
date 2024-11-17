package com.springproject.auctionplatform.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name="messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime time;

    @ManyToOne
    private User sender;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
}
