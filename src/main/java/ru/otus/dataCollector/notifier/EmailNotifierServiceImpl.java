package ru.otus.dataCollector.notifier;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.otus.dataCollector.model.domain.ContentRelease;
import ru.otus.dataCollector.repositories.SubscribedReleaseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailNotifierServiceImpl implements NotifierService {
    private final SubscribedReleaseRepository subscribedReleaseRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void notifySubscribers() {
        subscribedReleaseRepository.findAll().forEach(x -> sendEmail(x.getUserEmail(), x.getContentReleases()));
        subscribedReleaseRepository.deleteAll();
    }

    private void sendEmail(String email, List<ContentRelease> releases) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("the-useless-box@mail.ru");
        mailMessage.setFrom("the-useless-box@mail.ru");
        mailMessage.setSubject(email + " новый релиз ");
        StringBuilder stringBuilder = new StringBuilder();
        releases.forEach(x -> stringBuilder.append(x.getTitle()).append(" ").append(x.getInfoHash()).append(System.lineSeparator()));
        mailMessage.setText(stringBuilder.toString());
        mailSender.send(mailMessage);
    }
}
