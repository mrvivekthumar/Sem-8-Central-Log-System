package com.example.facultyservice.notification.controller;

import java.util.List;

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
    public Notification sendToMultiple(@RequestBody NotificationRequest notificationRequest,
            @RequestParam List<String> receiverIds) {
        return notificationService.sendNotificationToMultipleReceivers(notificationRequest, receiverIds);
    }

    @GetMapping("/{receiverId}")
    public List<Notification> getNotifications(@PathVariable String receiverId) {
        return notificationService.getNotificationsByReceiverId(receiverId);
    }
}
