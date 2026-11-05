package com.ordenDelAuror.aescipher.crypto;

public interface ProgressCallback {

    void update(long processed,
                long total);
}