package com.example.facultyservice.Feign;

import com.example.facultyservice.Dto.NotificationRequest;
import com.example.facultyservice.Model.Notification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationInterface {

    @PostMapping("/api/notification/send")
    ResponseEntity<Notification> sendNotification(@RequestBody NotificationRequest notificationRequest);

}
