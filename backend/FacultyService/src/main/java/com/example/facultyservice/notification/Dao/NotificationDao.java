package com.example.facultyservice.notification.Dao;

import com.example.facultyservice.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDao extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByTimestampDesc(String receiverId);
}
