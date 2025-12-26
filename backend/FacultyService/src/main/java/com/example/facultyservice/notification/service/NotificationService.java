package com.example.facultyservice.notification.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.facultyservice.notification.repository.NotificationRepository;
import com.example.facultyservice.notification.model.Notification;
import com.example.facultyservice.notification.model.NotificationRequest;

import jakarta.annotation.PostConstruct;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationDao;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("NotificationService initialized and ready!");
        logger.info("WebSocket messaging template configured");
        logger.info("=================================================");
    }

    public Notification sendNotification(NotificationRequest notificationRequest) {
        logger.info("===============================================");
        logger.info("NotificationService: Sending notification to single receiver");
        logger.info("Sender: {} (Type: {}), Receiver: {} (Type: {})",
                notificationRequest.getSenderId(),
                notificationRequest.getSenderType(),
                notificationRequest.getReceiverId(),
                notificationRequest.getReceiverType());
        logger.debug("Notification Type: {}, Title: {}",
                notificationRequest.getNotificationType(),
                notificationRequest.getTitle());
        logger.debug("Message: {}", notificationRequest.getMessage());

        try {
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

            logger.debug("Saving notification to database...");
            Notification savedNotification = notificationDao.save(notification);
            logger.info("Notification saved - ID: {}, Timestamp: {}",
                    savedNotification.getId(), savedNotification.getTimestamp());

            String topic = "/topic/notifications/" + notificationRequest.getReceiverId();
            logger.info("Sending WebSocket message to topic: {}", topic);
            messagingTemplate.convertAndSend(topic, savedNotification);
            logger.info("WebSocket message sent successfully to receiver: {}",
                    notificationRequest.getReceiverId());

            logger.info("Notification sent successfully - ID: {}", savedNotification.getId());
            logger.info("===============================================");
            return savedNotification;

        } catch (Exception e) {
            logger.error("Failed to send notification to receiver {}: {}",
                    notificationRequest.getReceiverId(), e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    public Notification sendNotificationToMultipleReceivers(NotificationRequest notificationRequest,
            List<String> receiverIds) {
        logger.info("===============================================");
        logger.info("NotificationService: Sending notification to MULTIPLE receivers");
        logger.info("Sender: {} (Type: {}), Number of Receivers: {}",
                notificationRequest.getSenderId(),
                notificationRequest.getSenderType(),
                receiverIds.size());
        logger.debug("Receiver IDs: {}", receiverIds);
        logger.debug("Notification Type: {}, Title: {}",
                notificationRequest.getNotificationType(),
                notificationRequest.getTitle());
        logger.debug("Message: {}", notificationRequest.getMessage());

        try {
            Notification notification = new Notification();
            notification.setSenderId(notificationRequest.getSenderId());
            notification.setSenderType(notificationRequest.getSenderType());
            notification.setReceiverType(notificationRequest.getReceiverType());
            notification.setNotificationType(notificationRequest.getNotificationType());
            notification.setMessage(notificationRequest.getMessage());
            notification.setTitle(notificationRequest.getTitle());
            notification.setSeen(false);
            notification.setTimestamp(LocalDateTime.now());

            int successCount = 0;
            int failCount = 0;

            logger.info("Starting batch notification send to {} receivers...", receiverIds.size());

            for (String receiverId : receiverIds) {
                try {
                    notification.setReceiverId(receiverId);

                    logger.debug("Saving notification for receiver: {}", receiverId);
                    Notification savedNotification = notificationDao.save(notification);
                    logger.debug("Notification saved for receiver {} - ID: {}",
                            receiverId, savedNotification.getId());

                    String topic = "/topic/notifications/" + receiverId;
                    logger.debug("Sending WebSocket message to topic: {}", topic);
                    messagingTemplate.convertAndSend(topic, savedNotification);
                    logger.debug("WebSocket message sent to receiver: {}", receiverId);

                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    logger.error("Failed to send notification to receiver {}: {}",
                            receiverId, e.getMessage());
                }
            }

            logger.info("Batch notification completed - Success: {}, Failed: {}, Total: {}",
                    successCount, failCount, receiverIds.size());
            logger.info("===============================================");

            return notification;

        } catch (Exception e) {
            logger.error("Failed to send batch notifications: {}", e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    public List<Notification> getNotificationsByReceiverId(String receiverId) {
        logger.info("===============================================");
        logger.info("NotificationService: Fetching notifications for receiver: {}", receiverId);

        try {
            logger.debug("Querying database for receiver ID: {}", receiverId);
            List<Notification> notifications = notificationDao.findByReceiverIdOrderByTimestampDesc(receiverId);

            logger.debug("Found {} notifications, sorting by timestamp...", notifications.size());
            notifications.sort(Comparator.comparing(Notification::getTimestamp).reversed());

            long unseenCount = notifications.stream().filter(n -> !n.getSeen()).count();
            logger.info("Fetched {} notifications for receiver {} ({} unseen)",
                    notifications.size(), receiverId, unseenCount);
            logger.info("===============================================");

            return notifications;

        } catch (Exception e) {
            logger.error("Failed to fetch notifications for receiver {}: {}",
                    receiverId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }
}
