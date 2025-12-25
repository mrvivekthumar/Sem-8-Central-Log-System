package com.example.facultyservice.notification.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.facultyservice.notification.repository.NotificationRepository;
import com.example.facultyservice.notification.model.Notification;
import com.example.facultyservice.notification.model.NotificationRequest;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationDao;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification sendNotification(NotificationRequest notificationRequest) {
        Notification notification = new Notification();
        notification.setSenderId(notificationRequest.getSenderId());
        notification.setSenderType(notificationRequest.getSenderType());
        notification.setReceiverId(notificationRequest.getReceiverId());
        notification.setReceiverType(notificationRequest.getReceiverType());
        notification.setNotificationType(notificationRequest.getNotificationType());
        notification.setMessage(notificationRequest.getMessage());
        notification.setTitle(notificationRequest.getTitle());
        notification.setSeen(false);
        notification.setTimestamp(LocalDateTime.now());

        Notification savedNotification = notificationDao.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + notificationRequest.getReceiverId(),
                savedNotification);

        return savedNotification;
    }

    public Notification sendNotificationToMultipleReceivers(NotificationRequest notificationRequest,
            List<String> receiverIds) {
        Notification notification = new Notification();
        notification.setSenderId(notificationRequest.getSenderId());
        notification.setSenderType(notificationRequest.getSenderType());
        notification.setReceiverType(notificationRequest.getReceiverType());
        notification.setNotificationType(notificationRequest.getNotificationType());
        notification.setMessage(notificationRequest.getMessage());
        notification.setTitle(notificationRequest.getTitle());
        notification.setSeen(false);
        notification.setTimestamp(LocalDateTime.now());

        for (String receiverId : receiverIds) {
            notification.setReceiverId(receiverId);
            Notification savedNotification = notificationDao.save(notification);

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + receiverId,
                    savedNotification);
        }

        return notification;
    }

    public List<Notification> getNotificationsByReceiverId(String receiverId) {
        List<Notification> notifications = notificationDao.findByReceiverIdOrderByTimestampDesc(receiverId);
        notifications.sort(Comparator.comparing(Notification::getTimestamp).reversed());
        return notifications;
    }
}
