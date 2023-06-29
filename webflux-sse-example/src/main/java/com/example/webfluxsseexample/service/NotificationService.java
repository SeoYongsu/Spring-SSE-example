package com.example.webfluxsseexample.service;

import com.example.webfluxsseexample.model.MessageStatus;
import com.example.webfluxsseexample.model.Notification;
import com.example.webfluxsseexample.payload.NotificationRequestData;
import com.example.webfluxsseexample.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;


    private final EmitterService emitterService;

    /**
     * 메시지 읽기
     */
    public Mono<Notification> getNotification(String notificationId, String username){
        return notificationRepository.findByIdAndUsername(notificationId, username)
                .doOnNext(notification -> {
                    notification.setStatus(MessageStatus.READ);
                    notificationRepository.save(notification).subscribe();
                });
    }

    /**
     * 알림 메세지 저장 및 Publish
     * @param username
     * @param data
     */
    public Mono<Notification> save(String username, NotificationRequestData data){
        Notification notification = Notification.builder()
                .type(data.getType())
                .username(username)
                .from(data.getFrom())
                .title(data.getTitle())
                .content(data.getContent())
                .build();

        return notificationRepository.save(notification);
    }


    public Flux<Notification> getNotificationList(String username){
        return notificationRepository.findNotificationsByUsername(username);
    }

}
