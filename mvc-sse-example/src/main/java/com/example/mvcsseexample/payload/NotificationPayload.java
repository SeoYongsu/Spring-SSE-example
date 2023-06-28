package com.example.mvcsseexample.payload;

import com.example.mvcsseexample.model.Notification;
import lombok.Data;

@Data
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
