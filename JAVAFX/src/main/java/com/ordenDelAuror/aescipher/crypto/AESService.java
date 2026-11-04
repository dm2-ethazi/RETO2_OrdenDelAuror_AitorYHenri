package com.ordenDelAuror.aescipher.crypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class AESService {

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    public static void encryptFile(
            File inputFile,
            File outputFile,
            String password) throws Exception {

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];

        SecureRandom random = new SecureRandom();

        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKey key = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec gcmSpec =
                new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        byte[] fileData =
                Files.readAllBytes(inputFile.toPath());

        byte[] encryptedData =
                cipher.doFinal(fileData);

        try (FileOutputStream fos =
                     new FileOutputStream(outputFile)) {

            fos.write(salt);
            fos.write(iv);
            fos.write(encryptedData);
        }
    }

    public static void decryptFile(
            File inputFile,
            File outputFile,
            String password) throws Exception {

        byte[] fileBytes =
                Files.readAllBytes(inputFile.toPath());

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];

        System.arraycopy(
                fileBytes, 0,
                salt, 0,
                SALT_LENGTH);

        System.arraycopy(
                fileBytes, SALT_LENGTH,
                iv, 0,
                IV_LENGTH);

        int encryptedLength =
                fileBytes.length
                        - SALT_LENGTH
                        - IV_LENGTH;

        byte[] encryptedData =
                new byte[encryptedLength];

        System.arraycopy(
                fileBytes,
                SALT_LENGTH + IV_LENGTH,
                encryptedData,
                0,
                encryptedLength);

        SecretKey key = generateKey(password, salt);

        Cipher cipher =
                Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec gcmSpec =
                new GCMParameterSpec(128, iv);

        cipher.init(
                Cipher.DECRYPT_MODE,
                key,
                gcmSpec);

        byte[] decryptedData =
                cipher.doFinal(encryptedData);

        Files.write(
                outputFile.toPath(),
                decryptedData);
    }

    private static SecretKey generateKey(
            String password,
            byte[] salt) throws Exception {

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                        "PBKDF2WithHmacSHA256");

        KeySpec spec =
                new PBEKeySpec(
                        password.toCharArray(),
                        salt,
                        ITERATIONS,
                        KEY_LENGTH);

        SecretKey tmp =
                factory.generateSecret(spec);

        return new SecretKeySpec(
                tmp.getEncoded(),
                "AES");
    }
}