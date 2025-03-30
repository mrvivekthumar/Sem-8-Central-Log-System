package com.example.notificationservice.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest{
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
