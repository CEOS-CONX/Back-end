package com.conx.server.global.mailSender;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class MailSender {
    @Value("${google.user}")
    String user;

    @Value("${google.password}")
    String password;

    public void sendMail(EmailDTO content) {
        String subject = content.getSubject();
        String text = content.getText();
        String receiver = content.getReceiver();

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.trust", "true");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (AddressException ae) {
            //TODO: 로그처리
            ae.printStackTrace();
            throw new CustomException(ErrorCode.INVALID_EMAIL_TYPE);
        } catch (AuthenticationFailedException aut){
            //TODO: 로그처리
            aut.printStackTrace();
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        } catch (SendFailedException se){
            //TODO: 로그처리
            se.printStackTrace();
            throw new CustomException(ErrorCode.SEND_FAILED);
        } catch (MessagingException me){
            //TODO: 로그처리
            me.printStackTrace();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}