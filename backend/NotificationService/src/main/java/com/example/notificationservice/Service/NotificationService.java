package com.example.notificationservice.Service;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.Dao.NotificationDao;
import com.example.notificationservice.model.NotificationRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public ResponseEntity<Notification> sendNotification(NotificationRequest notificationRequest) {
        try{
            Notification notification=new Notification();
            notification.setSenderId(notificationRequest.getSenderId());
            notification.setSenderType(notificationRequest.getSenderType());
            notification.setReceiverId(notificationRequest.getReceiverId());
            notification.setReceiverType(notificationRequest.getReceiverType());
            notification.setNotificationType(notificationRequest.getNotificationType());
            notification.setMessage(notificationRequest.getMessage());
            notification.setTitle(notificationRequest.getTitle());
            notificationDao.save(notification);
            simpMessagingTemplate.convertAndSend("/topic/notifications",notification);
            return new ResponseEntity<>(notification, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public ResponseEntity<Notification> sendNotificationToStudent(String studentId, NotificationRequest notificationRequest) {
        try {
            Notification notification = new Notification();
            notification.setSenderId(notificationRequest.getSenderId());
            notification.setSenderType(notificationRequest.getSenderType());
            notification.setReceiverId(studentId); // Ensure it is sent only to this student
            notification.setReceiverType(notificationRequest.getReceiverType());
            notification.setNotificationType(notificationRequest.getNotificationType());
            notification.setMessage(notificationRequest.getMessage());
            notification.setTitle(notificationRequest.getTitle());

            // Save to database
            notificationDao.save(notification);

            // Send notification only to the specified student
            simpMessagingTemplate.convertAndSendToUser(
                    studentId,  // Unique student identifier
                    "/queue/notifications",
                    notification
            );

            return new ResponseEntity<>(notification, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Notification>> getAlNotifications() {
        try{
            List<Notification> notifications = notificationDao.findAll();
            notifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            return new ResponseEntity<>(notifications,HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
