package com.example.facultyservice.notification.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.facultyservice.notification.model.Notification;
import com.example.facultyservice.notification.model.NotificationRequest;
import com.example.facultyservice.notification.service.NotificationService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("NotificationController initialized and ready!");
        logger.info("Endpoints: POST /send, POST /sendToMultiple, GET /{receiverId}");
        logger.info("=================================================");
    }

    @PostMapping("/send")
    public Notification sendNotification(@RequestBody NotificationRequest notificationRequest,
            HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: POST /api/notifications/send - Sending notification");
        logger.info("Sender: {} (Type: {}), Receiver: {} (Type: {})",
                notificationRequest.getSenderId(),
                notificationRequest.getSenderType(),
                notificationRequest.getReceiverId(),
                notificationRequest.getReceiverType());
        logger.debug("Title: {}, Type: {}",
                notificationRequest.getTitle(),
                notificationRequest.getNotificationType());
        logger.debug("Message: {}", notificationRequest.getMessage());
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            Notification notification = notificationService.sendNotification(notificationRequest);
            logger.info("Controller: Notification sent successfully - ID: {}", notification.getId());
            logger.info("===============================================");
            return notification;
        } catch (Exception e) {
            logger.error("Controller: Failed to send notification: {}", e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    @PostMapping("/sendToMultiple")
    public Notification sendToMultiple(@RequestBody NotificationRequest notificationRequest,
            @RequestParam List<String> receiverIds,
            HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: POST /api/notifications/sendToMultiple - Sending to multiple receivers");
        logger.info("Sender: {} (Type: {}), Number of Receivers: {}",
                notificationRequest.getSenderId(),
                notificationRequest.getSenderType(),
                receiverIds.size());
        logger.debug("Receiver IDs: {}", receiverIds);
        logger.debug("Title: {}, Type: {}",
                notificationRequest.getTitle(),
                notificationRequest.getNotificationType());
        logger.debug("Message: {}", notificationRequest.getMessage());
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            Notification notification = notificationService.sendNotificationToMultipleReceivers(
                    notificationRequest, receiverIds);
            logger.info("Controller: Batch notification sent to {} receivers", receiverIds.size());
            logger.info("===============================================");
            return notification;
        } catch (Exception e) {
            logger.error("Controller: Failed to send batch notifications: {}", e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }

    @GetMapping("/{receiverId}")
    public List<Notification> getNotifications(@PathVariable String receiverId,
            HttpServletRequest request) {
        logger.info("===============================================");
        logger.info("Controller: GET /api/notifications/{} - Fetching notifications", receiverId);
        logger.debug("Request from IP: {}", request.getRemoteAddr());

        try {
            List<Notification> notifications = notificationService.getNotificationsByReceiverId(receiverId);
            logger.info("Controller: Fetched {} notifications for receiver: {}",
                    notifications.size(), receiverId);
            logger.info("===============================================");
            return notifications;
        } catch (Exception e) {
            logger.error("Controller: Failed to fetch notifications for {}: {}",
                    receiverId, e.getMessage(), e);
            logger.info("===============================================");
            throw e;
        }
    }
}
