package com.conx.server.global.mailSender;

import lombok.Getter;

@Getter
public class EmailDTO {
    private EmailDTO(String from, String receiver, String text, String subject){
        this.from = from; this.receiver = receiver; this.text = text; this.subject = subject;
    }

    private String from;
    private String receiver;
    private String text;
    private String subject;

    public static EmailDTO create(String from, String receiver, String text, String subject){
        return new EmailDTO(from, receiver, text, subject);
    }

}
