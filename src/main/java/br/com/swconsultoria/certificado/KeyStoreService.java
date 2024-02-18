package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;

class KeyStoreService {

    private KeyStoreService() {}

    static KeyStore getKeyStoreA3(Certificado certificado) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore;
        Security.addProvider(certificado.getProvider());

        // Especifica o provedor ao carregar o KeyStore
        keyStore = KeyStore.getInstance("PKCS11", certificado.getProvider());
        keyStore.load(null, certificado.getSenha().toCharArray());
        return keyStore;
    }

    static KeyStore getKeyStoreRepositorioMac() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore;
        keyStore = KeyStore.getInstance("KeychainStore");
        keyStore.load(null, null);
        return keyStore;
    }

    static KeyStore getKeyStoreRepositorioWindows() throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore;
        keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
        keyStore.load(null, null);
        return keyStore;
    }

    static KeyStore getKeyStoreArquivoByte(byte[] certificado, Certificado certificado1) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (ByteArrayInputStream bs = new ByteArrayInputStream(certificado)) {
            keyStore.load(bs, certificado1.getSenha().toCharArray());
        }
        return keyStore;
    }

    static KeyStore getKeyStoreArquivo(Certificado certificado) throws CertificadoException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        File file = new File(certificado.getArquivo());
        if (!file.exists()) {
            throw new CertificadoException("Certificado Digital não Encontrado");
        }
        return getKeyStoreArquivoByte(Files.readAllBytes(file.toPath()), certificado);
    }

}
