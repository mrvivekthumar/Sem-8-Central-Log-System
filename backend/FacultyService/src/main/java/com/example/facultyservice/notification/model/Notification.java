package com.example.facultyservice.notification.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String senderId;
    
    @Enumerated(EnumType.STRING)
    private SenderType senderType;
    
    private String receiverId;
    
    @Enumerated(EnumType.STRING)
    private ReceiverType receiverType;
    
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    
    @Column(length = 1000)
    private String message;
    
    private String title;
    
    private Boolean seen = false;
    
    private LocalDateTime timestamp = LocalDateTime.now();
}
