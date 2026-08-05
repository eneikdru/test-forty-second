package com.eneik.generated.models.persistence;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
@Converter
public class SecureStringConverter implements AttributeConverter<String, String> {

    @Value("${security.encryption.password}")
    private String password;

    @Value("${security.encryption.salt}")
    private String salt;

    private TextEncryptor encryptor;

    @PostConstruct
    public void init() {
        // TextEncryptor is initialized exactly once at Spring context startup,
        // avoiding highly expensive PBKDF2 key derivation on every database operation.
        this.encryptor = Encryptors.text(password, salt);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        if (encryptor == null) {
            throw new IllegalStateException("SecureStringConverter encryptor has not been initialized by Spring.");
        }
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if (encryptor == null) {
            throw new IllegalStateException("SecureStringConverter encryptor has not been initialized by Spring.");
        }
        try {
            return encryptor.decrypt(dbData);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt secure field", e);
        }
    }
}
