package com.example.mvcsseexample.controller;

import com.example.mvcsseexample.model.Notification;
import com.example.mvcsseexample.model.NotificationRequestData;
import com.example.mvcsseexample.payload.NotificationPayload;
import com.example.mvcsseexample.service.EmitterService;
import com.example.mvcsseexample.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;
    private final EmitterService emitterService;


    @GetMapping(value = "/connect/{username}")
    public SseEmitter connect(@PathVariable("username")String username) throws IOException {
        SseEmitter sseEmitter = new SseEmitter(10000L);
        emitterService.addEmitter(username, sseEmitter);

        List<Notification> notificationList = notificationService.getNotificationList(username);
        if(notificationList.size()>0){
            for(Notification notification : notificationList){
                NotificationPayload payload = new NotificationPayload();
                payload.setNotification(notification);
                emitterService.push(username, payload);
            }
        }else{
            NotificationPayload payload = new NotificationPayload();
            emitterService.push(username, payload);
        }

        sseEmitter.onCompletion(() -> {
            log.info("onCompletion");
            emitterService.deleteKey(username);
        });
        sseEmitter.onTimeout(() -> {
            log.info("onTimeout");
            emitterService.deleteKey(username);
        });

        return sseEmitter;
    }


    @PostMapping("/push/{username}")
    public String push(@PathVariable("username") String username, @RequestBody NotificationRequestData requestData){
        Notification notification = notificationService.save(username, requestData);
        NotificationPayload payload = new NotificationPayload();
        payload.setNotification(notification);

        emitterService.push(username, payload);
        return "Push 성공";
    }
}
