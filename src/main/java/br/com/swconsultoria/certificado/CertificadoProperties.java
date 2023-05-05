package br.com.swconsultoria.certificado;

/**
 * Created by Samuel on 03/07/2017.
 */

import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.*;
import java.nio.file.Files;
import java.security.Security;

/**
 * Classe para uso exclusivo em caso do Erro Unknow CA
 */
class CertificadoProperties {

    static void inicia(Certificado certificado, InputStream iSCacert) throws CertificadoException, IOException {

        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
        System.clearProperty("javax.net.ssl.trustStore");

        System.setProperty("jdk.tls.client.protocols", "TLSv1.2"); // Servidor do	Sefaz RS

        switch (certificado.getTipoCertificado()) {
            case REPOSITORIO_WINDOWS:
                System.setProperty("javax.net.ssl.keyStoreProvider", "SunMSCAPI");
                System.setProperty("javax.net.ssl.keyStoreType", "Windows-MY");
                System.setProperty("javax.net.ssl.keyStoreAlias", certificado.getNome());
                break;
            case REPOSITORIO_MAC:
                System.setProperty("javax.net.ssl.keyStoreType", "KeychainStore");
                System.setProperty("javax.net.ssl.keyStoreAlias", certificado.getNome());
                break;
            case ARQUIVO:
                System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
                System.setProperty("javax.net.ssl.keyStore", certificado.getArquivo());
                break;
            case ARQUIVO_BYTES:
                File cert = File.createTempFile("cert", ".pfx");
                Files.write(cert.toPath(),certificado.getArquivoBytes());
                System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
                System.setProperty("javax.net.ssl.keyStore", cert.getAbsolutePath());
                break;
            case TOKEN_A3:
              throw new CertificadoException("Token A3 não pode utilizar Configuração através de Properties.");
        }

        System.setProperty("javax.net.ssl.keyStorePassword", certificado.getSenha());

        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        //Extrair Cacert do Jar
        String cacert;
        try {
            File file = File.createTempFile("tempfile", ".tmp");
            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = iSCacert.read(bytes)) !=
                    -1) {
                out.write(bytes, 0, read);
            }
            out.close();
            cacert = file.getAbsolutePath();
            file.deleteOnExit();
        } catch (IOException ex) {
            throw new CertificadoException(ex.getMessage(),ex);
        }

        System.setProperty("javax.net.ssl.trustStore", cacert);

    }

    public static void main(String[] args) throws IOException {
        File cert = File.createTempFile("cert", ".pfx");
        Files.write(cert.toPath(),Files.readAllBytes(new File("d:/certificado.pfx").toPath()));
        System.out.println(cert.getAbsolutePath());
    }
}
