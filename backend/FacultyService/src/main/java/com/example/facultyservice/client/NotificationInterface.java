package com.example.facultyservice.client;

import com.example.facultyservice.notification.model.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification-service.url:http://localhost:8084}")
public interface NotificationInterface {

    @PostMapping("/api/notifications/send")
    ResponseEntity<String> sendNotification(@RequestBody NotificationRequest notificationRequest);
}
