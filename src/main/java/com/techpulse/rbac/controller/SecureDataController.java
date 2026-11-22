package com.techpulse.rbac.controller;

import com.techpulse.rbac.constant.AppMessages;
import com.techpulse.rbac.dto.SecureDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secure-data")
public class SecureDataController {

    private static final Logger log =
            LoggerFactory.getLogger(SecureDataController.class);

    @GetMapping
    @PreAuthorize("hasPermission(null, 'READ_SECURE_DATA')")
    public ResponseEntity<SecureDataResponse> getSecureData() {
        log.info("Secure data response returned");
        return ResponseEntity.ok(
                new SecureDataResponse(
                        AppMessages.SECURE_DATA_ACCESS_GRANTED));
    }
}
