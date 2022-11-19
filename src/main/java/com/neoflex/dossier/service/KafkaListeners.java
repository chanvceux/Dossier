package com.neoflex.dossier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.dossier.dto.EmailMessageDTO;
import com.neoflex.dossier.feign_client.DealMC;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KafkaListeners {
    private final ObjectMapper objectMapper;
    private final SendMailServiceImpl mailService;
    private final DealMC dealMC;

    @KafkaListener(topics = {"finish-registration", "create-documents", "send-documents", "send-ses", "credit-issued", "application-denied"}, groupId = "groupId")
    public void listener(String str) throws JsonProcessingException {
        EmailMessageDTO emailMessageDTO = objectMapper.readValue(str, EmailMessageDTO.class);
        StringBuilder messageBody = new StringBuilder();

        switch (emailMessageDTO.getTheme()) {
            case FINISH_REGISTRATION -> {
                try {
                    messageBody.append("ЗАЯВКА НА ПОЛУЧЕНИЕ КРЕДИТА №")
                            .append(emailMessageDTO.getApplicationID())
                            .append(" успешно зарегистрирована\n")
                            .append("\nДля того, чтобы продолжить оформление, перейдите по ссылке: ")
                            .append("\nhttp://localhost:8084/swagger-ui/index.html#/deal-controller/calculate");
                    mailService.textMessageSending(emailMessageDTO.getAddress(), "ЗАЯВКА НА ПОЛУЧЕНИЕ КРЕДИТА", messageBody.toString());
                } catch (MailException mailException) {
                    mailException.printStackTrace();
                }
            }

            case CREATE_DOCUMENTS -> {
                try {
                    messageBody.append("СТАТУС ПО ЗАЯВКЕ НА КРЕДИТ №")
                            .append(emailMessageDTO.getApplicationID())
                            .append(" УСПЕШНО прошла проверку.\n")
                            .append("\nПерейдите по ссылке ниже для продолжения: ")
                            .append("\nhttp://localhost:8084/swagger-ui/index.html#/deal-controller/documentSend");
                    mailService.textMessageSending(emailMessageDTO.getAddress(), "СТАТУС ПО ЗАЯВКЕ НА КРЕДИТ", messageBody.toString());
                } catch (MailException mailException) {
                    mailException.printStackTrace();
                }
            }

            case SEND_DOCUMENTS -> {
                try {
                    messageBody.append("ДОКУМЕНТЫ ПО ЗАЯВКЕ № ")
                            .append(emailMessageDTO.getApplicationID())
                            .append("\n")
                            .append("Проверьте правильность документов, затем перейдите по ссылке ниже и вставьте номер заявки в поле для подтверждения с помощью кода: ")
                            .append("\nhttp://localhost:8084/swagger-ui/index.html#/deal-controller/documentSign");
                    mailService.documentMessageSending(emailMessageDTO.getAddress(), "ДОКУМЕНТЫ ПО ЗАЯВКЕ НА КРЕДИТ", messageBody.toString(), emailMessageDTO.getApplicationID());
                } catch (MailException | IOException mailException) {
                    mailException.printStackTrace(); }
            }
            case SEND_SES -> {
                try {
                    messageBody.append("КОД ПОДТВЕРЖДЕНИЯ ДЛЯ ЗАЯВКИ №")
                            .append(emailMessageDTO.getApplicationID())
                            .append("\nКод: ")
                            .append(dealMC.getApplication(emailMessageDTO.getApplicationID()).getSesCode())
                            .append("\n")
                            .append("Для того, чтобы подтвердить операцию, введите указанный код по ссылке ниже: ")
                            .append("\nhttp://localhost:8084/swagger-ui/index.html#/deal-controller/documentCode")
                            .append("\nЕсли вы не подавали заявку, обратитесь к оператору горячей линии по номеру +79876543210.");
                    mailService.textMessageSending(emailMessageDTO.getAddress(), "КОД ПОДТВЕРЖДЕНИЯ ЗАЯВКИ ПО КРЕДИТУ", messageBody.toString());
                } catch (MailException mailException) {
                    mailException.printStackTrace();
                }
            }
            case CREDIT_ISSUED -> {
                try {
                    messageBody.append("ЗАЯВКА НА КРЕДИТ №")
                            .append(emailMessageDTO.getApplicationID())
                            .append(" УСПЕШНО ОФОРМЛЕНА. \n")
                            .append("\nНачисление денежных средств на указанный счёт производится от 15 минут до 2 дней.")
                            .append("\nВ Случае возникновения вопросов или сложностей обратитесь к оператору горячей линии по номеру +79876543210.");

                    mailService.textMessageSending(emailMessageDTO.getAddress(), "ЗАЯВКА ПО КРЕДИТУ ОДОБРЕНА", messageBody.toString());
                } catch (MailException mailException) {
                    mailException.printStackTrace();
                }
            }
            case APPLICATION_DENIED -> {
                try {
                    messageBody.append("ЗАЯВКА НА КРЕДИТ №")
                            .append(emailMessageDTO.getApplicationID())
                            .append(" ОТКЛОНЕНА.")
                            .append("\nВ Случае возникновения вопросов обратитесь к оператору горячей линии по номеру +79876543210.");
                    mailService.textMessageSending(emailMessageDTO.getAddress(), "ЗАЯВКА ПО КРЕДИТУ ОТКЛОНЕНА", messageBody.toString());
                } catch (MailException mailException) {
                    mailException.printStackTrace();
                }
            }
        }
    }
}
