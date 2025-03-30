package com.example.facultyservice.Model;

import com.example.facultyservice.Dto.NotificationType;
import com.example.facultyservice.Dto.ReceiverType;
import com.example.facultyservice.Dto.SenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;


public class Notification {
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;
    private String senderId;
    @Enumerated(EnumType.STRING)
    private ReceiverType receiverType;
    private String receiverId;
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    private String title;
    private String message;
    private boolean isSeen;
//    @Column(name = "metadata", columnDefinition = "jsonb")
//    @Convert(converter = JsonConverter.class)
//    private Map<String, Object> metadata; // Dynamic metadata field

}
