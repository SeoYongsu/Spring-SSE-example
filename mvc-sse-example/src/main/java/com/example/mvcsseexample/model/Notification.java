package com.example.mvcsseexample.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@ToString
@Getter
@Document
@NoArgsConstructor
public class Notification {
    @Id
    private String id;
    
    /**
     * 수신자
     */
    @Indexed
    private String username;
    
    /**
     * 발송자
     */
    private String from;
    /**
     * 알림 타입
     */
    private MessageType type;


    /**
     * 알림 제목
     */
    private String title;
    /**
     * 알림 내용
     */
    private String content;

    /**
     * 메세지 상태
     */
    private MessageStatus status;

    /**
     * 보낸시간
     */
    private LocalDateTime timestamp;


    @Builder
    public Notification(String username, String from, String title, String content, MessageType type){
        this.username = username;
        this.from = from;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = MessageStatus.UN_READ;
        this.timestamp = LocalDateTime.now();
    }

    public void setStatus(MessageStatus status){
        this.status = status;
    }
}
