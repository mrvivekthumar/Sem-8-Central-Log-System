package com.example.facultyservice.notification.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.facultyservice.notification.model.Notification;

@Repository
public interface NotificationDao extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByTimestampDesc(String receiverId);
}
