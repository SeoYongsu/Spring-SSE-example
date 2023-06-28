package com.example.mvcsseexample.service;

import com.example.mvcsseexample.payload.NotificationPayload;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmitterService {

    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();


    public void addEmitter(String username, SseEmitter sseEmitter){
        sseEmitterMap.put(username, sseEmitter);
    }


    public void push(String username, NotificationPayload payload) {
        if(sseEmitterMap.containsKey(username)){
            SseEmitter sseEmitter = sseEmitterMap.get(username);
            try {
                sseEmitter.send(
                        SseEmitter
                        .event()
                        .name("notification")
                        .data(payload)
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void deleteKey(String username){
        sseEmitterMap.remove(username);
    }
}
