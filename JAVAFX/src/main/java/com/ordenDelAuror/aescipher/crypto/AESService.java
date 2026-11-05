package com.ordenDelAuror.aescipher.crypto;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

public class AESService {

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int BUFFER_SIZE = 8192;

    public static void encryptFile(
            File inputFile,
            File outputFile,
            String password,
            ProgressCallback callback)
            throws Exception
 {

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];

        SecureRandom random = new SecureRandom();

        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKey key =
                generateKey(password, salt);

        Cipher cipher =
                Cipher.getInstance(
                        "AES/CBC/PKCS5Padding");

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key,
                new IvParameterSpec(iv));

        try (
                FileInputStream fis =
                        new FileInputStream(inputFile);

                FileOutputStream fos =
                        new FileOutputStream(outputFile)
        ) {

            fos.write(salt);
            fos.write(iv);

            try (
                    CipherOutputStream cos =
                            new CipherOutputStream(
                                    fos,
                                    cipher)
            ) {

                byte[] buffer =
                        new byte[BUFFER_SIZE];

                int bytesRead;

                long totalSize = inputFile.length();
                long processed = 0;

                while ((bytesRead =
                        fis.read(buffer)) != -1) {

                    cos.write(
                            buffer,
                            0,
                            bytesRead);

                    processed += bytesRead;

                    callback.update(
                            processed,
                            totalSize);
                }
            }
        }
    }

    public static void decryptFile(
            File inputFile,
            File outputFile,
            String password,
            ProgressCallback callback)
            throws Exception {

        try (
                FileInputStream fis =
                        new FileInputStream(inputFile)
        ) {

            byte[] salt =
                    new byte[SALT_LENGTH];

            byte[] iv =
                    new byte[IV_LENGTH];

            fis.read(salt);
            fis.read(iv);

            SecretKey key =
                    generateKey(password, salt);

            Cipher cipher =
                    Cipher.getInstance(
                            "AES/CBC/PKCS5Padding");

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    new IvParameterSpec(iv));

            long totalSize =
                    inputFile.length();

            long processed =
                    SALT_LENGTH + IV_LENGTH;

            try (
                    CipherInputStream cis =
                            new CipherInputStream(
                                    fis,
                                    cipher);

                    FileOutputStream fos =
                            new FileOutputStream(
                                    outputFile)
            ) {

                byte[] buffer =
                        new byte[BUFFER_SIZE];

                int bytesRead;

                while ((bytesRead =
                        cis.read(buffer)) != -1) {

                    fos.write(
                            buffer,
                            0,
                            bytesRead);

                    processed += bytesRead;

                    callback.update(
                            processed,
                            totalSize);
                }
            }
        }
    }

    private static SecretKey generateKey(
            String password,
            byte[] salt) throws Exception {

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                        "PBKDF2WithHmacSHA256");

        PBEKeySpec spec =
                new PBEKeySpec(
                        password.toCharArray(),
                        salt,
                        ITERATIONS,
                        KEY_LENGTH);

        SecretKey temp =
                factory.generateSecret(spec);

        return new SecretKeySpec(
                temp.getEncoded(),
                "AES");
    }
}