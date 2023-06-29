package com.example.webfluxsseexample.payload;

import com.example.webfluxsseexample.model.MessageType;
import lombok.Data;

@Data
public class NotificationRequestData {
    /**
     * 알림 Type
     */
    private MessageType type;
    
    /**
     * 발송자
     */
    private String from;

    /**
     * 알림 제목
     */
    private String title;

    /**
     * 내용
     */
    private String content;
}
