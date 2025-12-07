package com.example.facultyservice.notification.Controller;

import com.example.facultyservice.notification.Service.NotificationService;
import com.example.facultyservice.notification.model.Notification;
import com.example.facultyservice.notification.model.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public Notification sendNotification(@RequestBody NotificationRequest notificationRequest) {
        return notificationService.sendNotification(notificationRequest);
    }

    @PostMapping("/sendToMultiple")
    public Notification sendToMultiple(@RequestBody NotificationRequest notificationRequest, @RequestParam List<String> receiverIds) {
        return notificationService.sendNotificationToMultipleReceivers(notificationRequest, receiverIds);
    }

    @GetMapping("/{receiverId}")
    public List<Notification> getNotifications(@PathVariable String receiverId) {
        return notificationService.getNotificationsByReceiverId(receiverId);
    }
}
