package com.example.mvcsseexample.service;

import com.example.mvcsseexample.model.MessageStatus;
import com.example.mvcsseexample.model.Notification;
import com.example.mvcsseexample.model.NotificationRequestData;
import com.example.mvcsseexample.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public Notification getNotification(String notificationId, String username){
        return notificationRepository.findNotificationByIdAndUsername(notificationId, username)
                .map(notification -> {
                    notification.setStatus(MessageStatus.READ);
                    return notificationRepository.save(notification);
                })
                .orElseThrow(() -> new RuntimeException("메세지가 없음"));
    }

    public Notification save(String username, NotificationRequestData data){
        Notification notification = Notification.builder()
                .type(data.getType())
                .username(username)
                .from(data.getFrom())
                .title(data.getTitle())
                .content(data.getContent())
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationList(String username){
        return notificationRepository.findNotificationsByUsername(username);
    }


}
