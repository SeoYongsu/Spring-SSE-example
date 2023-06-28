package com.example.webfluxsseexample.repository;

import com.example.webfluxsseexample.model.MessageStatus;
import com.example.webfluxsseexample.model.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {

    Mono<Notification> findByIdAndUsername(String notificationId, String username);


    Flux<Notification> findNotificationsByUsername(String username);

    Flux<Notification> findNotificationsByUsernameAndStatus(String username, MessageStatus status);

}
