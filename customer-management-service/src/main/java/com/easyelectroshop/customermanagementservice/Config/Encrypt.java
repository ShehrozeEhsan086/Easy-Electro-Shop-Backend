package com.easyelectroshop.customermanagementservice.Config;

import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SerializationUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

@Configuration
public class Encrypt implements AttributeConverter<String,String> {


    @Value("${encryptionKey}")
    private String encryptionKey;

    @Value("${encryptionCipher}")
    private String encryptionCipher;

    private Key key;
    private Cipher cipher;

    public Key getKey() {
        if( key == null ){
            key = new SecretKeySpec(encryptionKey.getBytes(),encryptionCipher);
        }
        return key;
    }

    public Cipher getCipher() throws GeneralSecurityException {
        if( cipher == null){
            cipher = Cipher.getInstance(encryptionCipher);
        }
        return cipher;
    }

    private void initCipher(int encryptMode) throws GeneralSecurityException {
        getCipher().init(encryptMode,getKey());
    }

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(String value) {
        if (value == null){
            return null;
        }
        initCipher(Cipher.ENCRYPT_MODE);
        byte[] bytes = value.getBytes();
        return Base64.getEncoder().encodeToString(getCipher().doFinal(bytes));
    }

    @SneakyThrows
    @Override
    public String convertToEntityAttribute(String value) {
        if (value == null){
            return null;
        }
        initCipher(Cipher.DECRYPT_MODE);
        byte[] bytes = getCipher().doFinal(Base64.getDecoder().decode(value));
        return SerializationUtils.deserialize(bytes).toString();
    }
}