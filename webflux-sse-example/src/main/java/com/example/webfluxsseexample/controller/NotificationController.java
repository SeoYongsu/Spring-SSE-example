package com.example.webfluxsseexample.controller;

import com.example.webfluxsseexample.model.Notification;
import com.example.webfluxsseexample.payload.NotificationPayload;
import com.example.webfluxsseexample.model.NotificationRequestData;
import com.example.webfluxsseexample.payload.SseStatus;
import com.example.webfluxsseexample.service.EmitterService;
import com.example.webfluxsseexample.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final EmitterService emitterService;
    private final NotificationService notificationService;


    @GetMapping(value = "/connect/{username}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationPayload>> connect(@PathVariable("username") String username,
                                                              ServerHttpRequest request) {

        HttpHeaders headers = request.getHeaders();
        log.info("해더 확인 예제~ : {}",headers);
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        log.info("authorization -> {}",authorization);

        Sinks.Many<NotificationPayload> sinks = Sinks.many().multicast().onBackpressureBuffer();


        return notificationService.getNotificationList(username)
                .map(notification -> {
                    NotificationPayload payload = new NotificationPayload();
                    payload.setStatus(SseStatus.CONNECT);
                    payload.setNotification(notification);
                    return payload;
                })
                .switchIfEmpty(Mono.defer(()->{
                    NotificationPayload payload = new NotificationPayload();
                    payload.setStatus(SseStatus.CONNECT);
                    return Mono.just(payload);
                }))
                .doFirst(()-> emitterService.addSinks(username, sinks))
                .doOnNext(sinks::tryEmitNext)
                .thenMany(sinks.asFlux())
                .timeout(Duration.ofSeconds(10))
                .map(payload ->
                        ServerSentEvent.<NotificationPayload>builder()
                                .id(String.valueOf(username))
                                .event("notification")
                                .data(payload)
                                .build()
                )
                .onErrorResume(TimeoutException.class, ex->
                        Mono.just(
                                ServerSentEvent.<NotificationPayload>builder()
                                        .id(String.valueOf(username))
                                        .event(SseStatus.DIS_CONNECT.toString())
                                        .build()
                        )
                )
                .doFinally(signalType -> {
                    switch (signalType){
                        case CANCEL ->
                                log.info("doFinally CANCEL");
                        case ON_COMPLETE ->
                                log.info("doFinally ONCOMPLETE");

                        default ->
                                log.info("doFinally Default");
                    }
                });
    }

    @PostMapping("/push/{username}")
    public Mono<String> push(@PathVariable("username") String username, @RequestBody NotificationRequestData requestData){
        log.info("push 접속");
        return notificationService.save(username, requestData)
                .flatMap(notification -> {
                    NotificationPayload payload = new NotificationPayload();
                    payload.setStatus(SseStatus.NEW);
                    payload.setNotification(notification);
                    emitterService.push(username, payload);
                    return Mono.just("완료");
                })
                .onErrorResume(throwable -> {
                    return Mono.error(throwable);
                });
    }


}
