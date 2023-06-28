package com.example.mvcsseexample.repository;

import com.example.mvcsseexample.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {


    Optional<Notification> findNotificationByIdAndUsername(String notificationId, String username);

    List<Notification> findNotificationsByUsername(String username);

}
