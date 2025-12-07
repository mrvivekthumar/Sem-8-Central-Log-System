package com.example.facultyservice.notification.model;

import lombok.Data;

@Data
public class NotificationRequest {
    private String senderId;
    private SenderType senderType;
    private String receiverId;
    private ReceiverType receiverType;
    private NotificationType notificationType;
    private String message;
    private String title;
    private Boolean seen = false;
}
