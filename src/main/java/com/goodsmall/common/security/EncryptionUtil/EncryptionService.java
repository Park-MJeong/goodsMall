package com.goodsmall.common.security.EncryptionUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptionService {
    private final EncryptionUtil encryptionUtil;
    private final PasswordEncoder passwordEncoder;

    public String encryptName(String userName) {
        return encryptionUtil.encrypt(userName);
    }
    public String encryptPhone(String phoneNumber) {
        return encryptionUtil.encrypt(phoneNumber);
    }

    public String encryptAddress(String address) {
        return encryptionUtil.encrypt(address);
    }

    public String encryptEmail(String email) {
        return encryptionUtil.encrypt(email);
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public String decryptEmail(String encryptedEmail) {
        return encryptionUtil.decrypt(encryptedEmail);
    }

    public String decryptAddress(String encryptedAddress) {
        return encryptionUtil.decrypt(encryptedAddress);
    }

    public String decryptName(String encryptedUserName) {
        return encryptionUtil.decrypt(encryptedUserName);
    }
}

