package com.neoflex.dossier.feign_client;

import com.neoflex.dossier.dto.DocumentCreatingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "Deal", url = "${deal.url}" )
public interface DealMC {

    @PostMapping("/deal/application/{applicationId}")
    DocumentCreatingDTO getApplication(@PathVariable Long applicationId);

}
