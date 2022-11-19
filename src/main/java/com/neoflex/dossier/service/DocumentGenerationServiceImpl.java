package com.neoflex.dossier.service;

import com.neoflex.dossier.dto.DocumentCreatingDTO;
import com.neoflex.dossier.dto.PaymentScheduleElementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentGenerationServiceImpl implements DocumentGenerationService {
    public File createCreditApplicationDocument(DocumentCreatingDTO documentCreatingDTO, Long applicationId) {

        StringBuilder stringBuilder = new StringBuilder("Заявка на кредит №" + applicationId + " от " + LocalDateTime.now())
                .append("\n\nИнформация о клиенте")
                .append("\n\tПолное имя: ").append(documentCreatingDTO.getFullName())
                .append("\n\tДата рождения: ").append(documentCreatingDTO.getBirthdate())
                .append("\n\tПол: ").append(documentCreatingDTO.getGender())
                .append("\n\tПаспорт: ").append(documentCreatingDTO.getFullPassportData())
                .append("\n\tEmail: ").append(documentCreatingDTO.getEmail())
                .append("\n\tСемейное положение: ").append(documentCreatingDTO.getMartialStatus())
                .append("\n\tКоличество иждивенцев: ").append(documentCreatingDTO.getDependentAmount())
                .append("\n\nИнформация о текущей трудовой деятельности клиента")
                .append("\n\tРабочий статус: ").append(documentCreatingDTO.getEmployment().getEmploymentStatus())
                .append("\n\tЗаработная плата: ").append(documentCreatingDTO.getEmployment().getSalary())
                .append("\n\tЗанимаемая должность: ").append(documentCreatingDTO.getEmployment().getPosition())
                .append("\n\tОбщий опыт работы: ").append(documentCreatingDTO.getEmployment().getWorkExperienceTotal())
                .append("\n\tТекущий опыт работы: ").append(documentCreatingDTO.getEmployment().getWorkExperienceCurrent());

        log.trace("FILE <CREDIT_APPLICATION> CREATED, VALUE: {}", stringBuilder);
        return creatingDocument(stringBuilder, "credit_application");
    }

    public File createCreditContractDocument(DocumentCreatingDTO documentCreatingDTO, Long applicationId) {

        StringBuilder stringBuilder = new StringBuilder("Заявка на кредит №" + applicationId + " от " + LocalDateTime.now())
                .append("\n\nПолное имя клиента: ").append(documentCreatingDTO.getFullName())
                .append("\nПаспорт клиента: ").append(documentCreatingDTO.getFullPassportData())
                .append("\n\nИнформация о работе кредите")
                .append("\n\tСумма кредита: ").append(documentCreatingDTO.getAmount().toString())
                .append("\n\tСрок кредита: ").append(documentCreatingDTO.getTerm())
                .append("\n\tЕжемесячный платёж: ").append(documentCreatingDTO.getMonthlyPayment())
                .append("\n\tПроцентная ставка: ").append(documentCreatingDTO.getRate())
                .append("\n\tПолная стоимость кредита: ").append(documentCreatingDTO.getPsk())
                .append("\n\tСтраховка включена: ").append(documentCreatingDTO.getIsInsuranceEnabled())
                .append("\n\tЗарплатный клиент: ").append(documentCreatingDTO.getIsInsuranceEnabled());

        log.trace("FILE <CREDIT_CONTRACT> CREATED, VALUE: {}", stringBuilder);
        return creatingDocument(stringBuilder, "credit_contract");

    }
    public File createCreditPaymentScheduleDocument(DocumentCreatingDTO documentCreatingDTO, Long applicationId) {
        StringBuilder stringBuilder = new StringBuilder("График ежемесячных платежей по договору № " + applicationId + " от " + LocalDateTime.now());

        for (PaymentScheduleElementDTO paymentScheduleElementDTO : documentCreatingDTO.getPaymentScheduleElementList()) {
            stringBuilder.append("\nМесяц платежа № ").append(paymentScheduleElementDTO.getNumber())
                    .append("\n\tДата платежа: ").append(paymentScheduleElementDTO.getDate().toString())
                    .append("\n\tЕжемесячный платёж: ").append(paymentScheduleElementDTO.getTotalPayment())
                    .append("\n\tПогашение процентов: ").append(paymentScheduleElementDTO.getInterestPayment())
                    .append("\n\tПогашение основного долга: ").append(paymentScheduleElementDTO.getDebtPayment())
                    .append("\n\tОстаток долга: ").append(paymentScheduleElementDTO.getRemainingDebt());
        }

        log.trace("FILE <CREDIT_PAYMENT_SCHEDULE> CREATED, VALUE: {}", stringBuilder);
        return creatingDocument(stringBuilder, "credit_payment_schedule");
    }

    private File creatingDocument (StringBuilder stringBuilder, String docName) {
        File document = null;
        try {
            document = File.createTempFile(docName, ".txt", new File("src/main/resources"));
            try (FileWriter fileWriter = new FileWriter(document)) {
                fileWriter.write(stringBuilder.toString());
                fileWriter.flush();
            }
            log.trace("DOCUMENT {} CREATED AND SAVED", docName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }
}