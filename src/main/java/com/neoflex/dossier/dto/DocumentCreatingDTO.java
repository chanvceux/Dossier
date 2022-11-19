package com.neoflex.dossier.dto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DocumentCreatingDTO {

    String fullName;
    LocalDate birthdate;
    String gender;
    String fullPassportData;
    String email;
    String martialStatus;
    Integer dependentAmount;
    EmploymentDTO employment;
    BigDecimal amount;
    Integer term;
    BigDecimal monthlyPayment;
    BigDecimal rate;
    BigDecimal psk;
    Boolean isInsuranceEnabled;
    Boolean isSalaryClient;
    List<PaymentScheduleElementDTO> paymentScheduleElementList;
    Integer sesCode;

}
