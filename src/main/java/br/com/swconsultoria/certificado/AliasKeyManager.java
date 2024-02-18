package br.com.swconsultoria.certificado;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 03/07/2019 - 02:00
 */

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

@AllArgsConstructor
@Log
class AliasKeyManager implements X509KeyManager {

    private KeyStore ks;
    private String alias;
    private String password;

    public String chooseClientAlias(String[] str, Principal[] principal, Socket socket) {
        return alias;
    }

    public String chooseServerAlias(String str, Principal[] principal, Socket socket) {
        return alias;
    }

    public String[] getClientAliases(String str, Principal[] principal) {
        return new String[]{alias};
    }

    public String[] getServerAliases(String str, Principal[] principal) {
        return new String[]{alias};
    }

    public X509Certificate[] getCertificateChain(String alias) {
        try {
            Certificate[] certificates = this.ks.getCertificateChain(alias);
            X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
            System.arraycopy(certificates, 0, x509Certificates, 0, certificates.length);
            return x509Certificates;
        } catch (KeyStoreException e) {
           log.severe("Não foi possível carregar o keystore para o alias:" + alias);
        }

        return new X509Certificate[0];
    }

    public PrivateKey getPrivateKey(String alias) {
        try {
            return (PrivateKey) ks.getKey(alias, password == null
                    ? null : password.toCharArray());
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return null;
    }
}