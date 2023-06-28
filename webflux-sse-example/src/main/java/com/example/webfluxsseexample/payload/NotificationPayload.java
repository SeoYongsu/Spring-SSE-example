package com.example.webfluxsseexample.payload;

import com.example.webfluxsseexample.model.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationPayload {

    /**
     * Sse 메세지 상태 확인용
     */
    private SseStatus status;


    /**
     * 알림데이터
     */
    private Notification notification;



}
