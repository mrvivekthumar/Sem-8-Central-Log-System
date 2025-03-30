package com.example.notificationservice.Dao;

import com.example.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDao extends JpaRepository<Notification,Integer> {
    List<Notification> findAllByOrderByTimestampDesc();
}
