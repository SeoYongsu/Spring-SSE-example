package com.example.webfluxsseexample.controller;

import com.example.webfluxsseexample.model.Notification;
import com.example.webfluxsseexample.model.NotificationRequestData;
import com.example.webfluxsseexample.payload.NotificationPayload;
import com.example.webfluxsseexample.payload.SseStatus;
import com.example.webfluxsseexample.service.EmitterService;
import com.example.webfluxsseexample.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component("NotificationHandler")
@RequiredArgsConstructor
@Slf4j
public class NotificationHandler {

    private final EmitterService emitterService;
    private final NotificationService notificationService;

    public Mono<ServerResponse> connect(ServerRequest request){
        log.info("handler ");
        String username = request.pathVariable("username");
        Sinks.Many<NotificationPayload> sinks = Sinks.many().multicast().onBackpressureBuffer();

        Flux<ServerSentEvent<NotificationPayload>> stream =  notificationService.getNotificationList(username)
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

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(stream, NotificationPayload.class);
    }

    public Mono<ServerResponse> push(ServerRequest request){
        String username = request.pathVariable("username");
        Mono<NotificationRequestData> requestBody = request.bodyToMono(NotificationRequestData.class);
        
        return requestBody
                .flatMap(requestData ->
                     notificationService.save(username, requestData)
                            .flatMap(notification -> {
                                NotificationPayload payload = new NotificationPayload();
                                payload.setStatus(SseStatus.NEW);
                                payload.setNotification(notification);
                                emitterService.push(username, payload);
                                return ServerResponse.ok().bodyValue("성공");
                            })
                            .onErrorResume(Mono::error)
                );

    }


}
