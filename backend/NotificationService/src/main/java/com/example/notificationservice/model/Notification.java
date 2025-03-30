package com.example.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
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
    @CreationTimestamp
    private LocalDateTime timestamp;

//    @Column(name = "metadata", columnDefinition = "jsonb")
//    @Convert(converter = JsonConverter.class)
//    private Map<String, Object> metadata; // Dynamic metadata field

}
