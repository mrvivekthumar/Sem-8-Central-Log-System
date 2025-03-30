package com.example.notificationservice.Controller;

import com.example.notificationservice.Service.NotificationService;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        System.out.println("Success Bro");
        return notificationService.sendNotification(notificationRequest);

    }
    @PostMapping("/send-to-student/{studentId}")
    public ResponseEntity<Notification> sendNotificationToStudent(
            @PathVariable String studentId,
            @RequestBody NotificationRequest notificationRequest) {
        return notificationService.sendNotificationToStudent(studentId, notificationRequest);
    }
    @GetMapping("/all")
    ResponseEntity<List<Notification>> getAlllNotifications(){
        return notificationService.getAlNotifications();
    }

}
