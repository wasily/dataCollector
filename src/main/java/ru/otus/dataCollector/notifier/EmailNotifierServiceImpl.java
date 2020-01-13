package ru.otus.dataCollector.notifier;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.otus.dataCollector.configuraton.ApplicationConfiguration;
import ru.otus.dataCollector.model.domain.ContentRelease;
import ru.otus.dataCollector.model.domain.SubscribedRelease;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailNotifierServiceImpl implements NotifierService {
    private final ApplicationConfiguration applicationConfiguration;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void notifySubscriber(SubscribedRelease subscribedRelease) {
        sendEmail(subscribedRelease.getUserEmail(), subscribedRelease.getContentReleases());
    }

    private void sendEmail(String email, List<ContentRelease> releases) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setFrom(applicationConfiguration.getServerEmail());
        mailMessage.setSubject("Для Вас найден новый релиз");
        StringBuilder stringBuilder = new StringBuilder();
        releases.forEach(x -> stringBuilder.append("➤").append(x.getTitle()).append(" => ")
                .append(x.getInfoHash()).append(System.lineSeparator()));
        mailMessage.setText(stringBuilder.toString());
        mailSender.send(mailMessage);
    }
}
