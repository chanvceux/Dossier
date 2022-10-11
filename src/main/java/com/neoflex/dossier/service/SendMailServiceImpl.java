package com.neoflex.dossier.service;

import com.neoflex.dossier.dto.DocumentCreatingDTO;
import com.neoflex.dossier.feign_client.DealMC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SendMailServiceImpl {
    private final JavaMailSender mailSender;
    private final DealMC dealMC;
    private final SimpleMailMessage templateMessage = new SimpleMailMessage();
    private final DocumentGenerationServiceImpl documentGenerationService;

    @Autowired
    public SendMailServiceImpl(JavaMailSender mailSender, DealMC dealMC, DocumentGenerationServiceImpl documentGenerationService) {
        this.mailSender = mailSender;
        this.dealMC = dealMC;
        this.documentGenerationService = documentGenerationService;
    }

    public void textMessageSending(String to, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage(templateMessage);
        simpleMailMessage.setFrom("neotesingdos@mail.ru");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        mailSender.send(simpleMailMessage);
    }

    public void documentMessageSending(String to, String subject, String text, Long id) throws IOException {
            DocumentCreatingDTO summaryInfo = dealMC.getApplication(id);

            File creditApplicationDocument = documentGenerationService.createCreditApplicationDocument(summaryInfo, id);
            File creditContractDocument = documentGenerationService.createCreditContractDocument(summaryInfo, id);
            File creditPaymentScheduleDocument = documentGenerationService.createCreditPaymentScheduleDocument(summaryInfo, id);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper;

            try {
                helper = new MimeMessageHelper(message, true);
                helper.setFrom("neotesingdos@mail.ru");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(text);
                helper.addAttachment(creditApplicationDocument.getName(), creditApplicationDocument);
                helper.addAttachment(creditContractDocument.getName(), creditContractDocument);
                helper.addAttachment(creditPaymentScheduleDocument.getName(), creditPaymentScheduleDocument);
                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            } finally {
                Files.delete(Path.of(creditApplicationDocument.getPath()));
                Files.delete(Path.of(creditContractDocument.getPath()));
                Files.delete(Path.of(creditPaymentScheduleDocument.getPath()));
            }
        }
}
