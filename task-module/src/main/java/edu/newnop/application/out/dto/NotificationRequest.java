package edu.newnop.application.out.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest<T> {
    private String receiverEmail;
    private String receiverName;
    private String message;
    private String subject;
    private T data;
}

