package com.example.webfluxsseexample.service;

import com.example.webfluxsseexample.payload.NotificationPayload;
import com.example.webfluxsseexample.payload.SseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class EmitterService {

    /**
     * 임시 저장소 ConcurrentHashMap
     * 추후 Redis 또는 kafka로 이동 필요함.
     * Key   : username -> Email
     * Value : Sinks.Many<NotificationPayload>
     */
    private final Map<String, Sinks.Many<NotificationPayload>> notificationSinks = new ConcurrentHashMap<>();


    /**
     * Sinks 생성
     */
    public Sinks.Many<NotificationPayload> createSinks(String username){
        Sinks.Many<NotificationPayload> sinks = Sinks.many().multicast().onBackpressureBuffer();
        notificationSinks.put(username, sinks);
        return sinks;
    }

    /**
     * notificationSinks Add Sinks
     */
    public void addSinks(String username, Sinks.Many<NotificationPayload> sinks){
        notificationSinks.put(username, sinks);
    }

    public Sinks.Many<NotificationPayload> getSinks(String username){
        return notificationSinks.get(username);
    }


    /**
     * Sinks Flux 반환
     */
    public Flux<NotificationPayload> getFluxPayload(String username){
        return this.createSinks(username).asFlux();
    }


    /**
     * NotificationPayload Push
     * SSE : publish
     * tryEmitNext : 비동기 처리  return EmitResult
     * emitNext    : 동기  처리   return Void
     *               Handler를 등록하여 에러 처리 가능
     */
    public void push(String username, NotificationPayload payload){
        if(notificationSinks.containsKey(username)){
            Sinks.Many<NotificationPayload> sinks = notificationSinks.get(username);
            Sinks.EmitResult result = sinks.tryEmitNext(payload);
//            sinks.emitNext(payload, null);
        }
    }


    /**
     * Key 제거
     */
    public void delete(String username){
        log.info("Deleted Sinks for {}", username);
        notificationSinks.remove(username);
    }
}
