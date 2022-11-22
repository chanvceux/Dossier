package com.neoflex.dossier.dto;

import com.neoflex.dossier.enumeration.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class EmailMessageDTO {
    String address;
    Theme theme;
    Long applicationID;

}
