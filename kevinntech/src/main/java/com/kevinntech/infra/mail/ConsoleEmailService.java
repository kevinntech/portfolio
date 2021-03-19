package com.kevinntech.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
public class ConsoleEmailService implements EmailService{

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("보낸 이메일 메시지: {}", emailMessage.getMessage());
    }

}
