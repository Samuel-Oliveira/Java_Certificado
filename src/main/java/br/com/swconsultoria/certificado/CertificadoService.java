package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.certificado.util.DocumentoUtil;
import lombok.extern.java.Log;
import org.apache.commons.httpclient.protocol.Protocol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SuppressWarnings("WeakerAccess")
@Log
public class CertificadoService {

    private static final String SENHA_NAO_PODE_SER_NULA = "Senha não pode ser nula.";
    private static final String CERTIFICADO_NAO_PODE_SER_NULO = "Certificado não pode ser nulo.";
    private static final String ERRO_AO_CARREGAR_INFORMACOES_DO_CERTIFICADO = "Erro ao carregar informações do certificado:";
    private static boolean cacertProprio;

    private CertificadoService() {
    }

    public static void inicializaCertificado(Certificado certificado) throws CertificadoException {
        cacertProprio = true;
        inicializaCertificado(certificado, CertificadoService.class.getResourceAsStream("/cacert"));
    }

    public static void inicializaCertificado(Certificado certificado, InputStream cacert) throws CertificadoException {

        try {

            KeyStore keyStore = getKeyStore(
                    Optional.ofNullable(certificado).orElseThrow(() -> new IllegalArgumentException(CERTIFICADO_NAO_PODE_SER_NULO)));
            SocketFactoryDinamico socketFactory = new SocketFactoryDinamico(keyStore, certificado.getNome(), certificado.getSenha(),
                    Optional.ofNullable(cacert).orElseThrow(() -> new IllegalArgumentException("Cacert não pode ser nulo.")),
                    certificado.getSslProtocol());
            Protocol protocol = new Protocol("https", socketFactory, 443);
            Protocol.registerProtocol("https", protocol);

            log.info(String.format("JAVA-CERTIFICADO | Samuel Oliveira | samuel@swconsultoria.com.br " +
                            "| VERSAO=%s | DATA_VERSAO=%s | CNPJ/CPF=%s | VENCIMENTO=%s | ALIAS=%s | TIPO=%s | CAMINHO=%s | CACERT=%s | SSL=%s",
                    "3.7",
                    "11/07/2024",
                    certificado.getCnpjCpf(),
                    certificado.getDataHoraVencimento(),
                    certificado.getNome().toUpperCase(),
                    certificado.getTipoCertificado().toString(),
                    certificado.getArquivo(),
                    cacertProprio ? "Default" : "Customizado",
                    certificado.getSslProtocol()));

        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException | CertificateException |
                 IOException e) {
            throw new CertificadoException(e.getMessage(), e);
        }

    }

    public static Certificado certificadoPfxBytes(byte[] certificadoBytes, String senha) throws CertificadoException {

        Certificado certificado = new Certificado();
        try {
            certificado.setArquivoBytes(Optional.ofNullable(certificadoBytes).orElseThrow(() -> new IllegalArgumentException(CERTIFICADO_NAO_PODE_SER_NULO)));
            certificado.setSenha(Optional.ofNullable(senha).orElseThrow(() -> new IllegalArgumentException(SENHA_NAO_PODE_SER_NULA)));
            certificado.setTipoCertificado(TipoCertificadoEnum.ARQUIVO_BYTES);
            setDadosCertificado(certificado, null);
        } catch (KeyStoreException e) {
            throw new CertificadoException(ERRO_AO_CARREGAR_INFORMACOES_DO_CERTIFICADO + e.getMessage(), e);
        }

        return certificado;

    }

    private static void setDadosCertificado(Certificado certificado, KeyStore keyStore) throws CertificadoException, KeyStoreException {

        if (keyStore == null) {
            keyStore = getKeyStore(certificado);
            Enumeration<String> aliasEnum = keyStore.aliases();
            String aliasKey = aliasEnum.nextElement();
            certificado.setNome(aliasKey);
        }

        X509Certificate certificate = getCertificate(certificado, keyStore);
        certificado.setCnpjCpf(
                Optional.ofNullable(certificate.getExtensionValue("2.5.29.17"))
                        .flatMap(DocumentoUtil::getDocumentoFromCertificado)
                        .orElse(""));
        Date dataValidade = dataValidade(certificate);
        certificado.setVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        certificado.setDataHoraVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        certificado.setDiasRestantes(diasRestantes(certificado));
        certificado.setValido(valido(certificado));
        certificado.setNumeroSerie(certificate.getSerialNumber());
    }

    public static Certificado certificadoPfx(String caminhoCertificado, String senha) throws CertificadoException, FileNotFoundException {

        if (!Files.exists(Paths.get(
                Optional.ofNullable(caminhoCertificado).orElseThrow(() -> new IllegalArgumentException("Caminho do Certificado não pode ser nulo.")))))
            throw new FileNotFoundException("Arquivo " +
                    caminhoCertificado +
                    " não existe");

        Certificado certificado = new Certificado();

        try {
            certificado.setArquivo(caminhoCertificado);
            certificado.setSenha(Optional.ofNullable(senha).orElseThrow(() -> new IllegalArgumentException(SENHA_NAO_PODE_SER_NULA)));
            certificado.setTipoCertificado(TipoCertificadoEnum.ARQUIVO);
            setDadosCertificado(certificado, null);
        } catch (KeyStoreException e) {
            throw new CertificadoException(ERRO_AO_CARREGAR_INFORMACOES_DO_CERTIFICADO +
                    e.getMessage(), e);
        }

        return certificado;
    }

    public static Certificado certificadoA3(String senha, Provider provider) throws CertificadoException {

        try {
            Certificado certificado = new Certificado();
            certificado.setTipoCertificado(TipoCertificadoEnum.TOKEN_A3);
            certificado.setSenha(Optional.ofNullable(senha).orElseThrow(() -> new IllegalArgumentException(SENHA_NAO_PODE_SER_NULA)));
            certificado.setProvider(Optional.ofNullable(provider).orElseThrow(() -> new IllegalArgumentException("Provider não pode ser nulo.")));
            setDadosCertificado(certificado, null);
            return certificado;

        } catch (Exception e) {
            throw new CertificadoException(ERRO_AO_CARREGAR_INFORMACOES_DO_CERTIFICADO +
                    e.getMessage(), e);
        }

    }

    public static List<Certificado> listaCertificadosWindows(boolean listarVencidos) throws CertificadoException {
        return listaCertificadosRepositorio(TipoCertificadoEnum.REPOSITORIO_WINDOWS, listarVencidos);
    }

    public static List<Certificado> listaCertificadosMac(boolean listarVencidos) throws CertificadoException {
        return listaCertificadosRepositorio(TipoCertificadoEnum.REPOSITORIO_MAC, listarVencidos);
    }

    public static List<Certificado> listaCertificadosWindows() throws CertificadoException {
        return listaCertificadosRepositorio(TipoCertificadoEnum.REPOSITORIO_WINDOWS, true);
    }

    public static List<Certificado> listaCertificadosMac() throws CertificadoException {
        return listaCertificadosRepositorio(TipoCertificadoEnum.REPOSITORIO_MAC, true);
    }

    private static List<Certificado> listaCertificadosRepositorio(TipoCertificadoEnum tipo, boolean listarVencidos) throws CertificadoException {

        List<Certificado> listaCert = new ArrayList<>();
        Certificado cert = new Certificado();
        cert.setTipoCertificado(tipo);
        try {
            KeyStore ks = getKeyStore(cert);
            Enumeration<String> aliasEnum = ks.aliases();
            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();
                if (aliasKey != null) {
                    Certificado certificado = new Certificado();
                    certificado.setTipoCertificado(tipo);
                    certificado.setNome(aliasKey);
                    setDadosCertificado(certificado, ks);
                    if (listarVencidos) {
                        listaCert.add(certificado);
                    } else {
                        if (certificado.isValido()) {
                            listaCert.add(certificado);
                        }
                    }
                }
            }
        } catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados:" +
                    ex.getMessage(), ex);
        }
        return listaCert;
    }

    public static List<String> listaAliasCertificadosA3(String senha, Provider provider) throws CertificadoException {

        try {
            List<String> listaCert = new ArrayList<>(20);
            Certificado certificado = new Certificado();
            certificado.setTipoCertificado(TipoCertificadoEnum.TOKEN_A3);
            certificado.setSenha(Optional.ofNullable(senha).orElseThrow(() -> new IllegalArgumentException(SENHA_NAO_PODE_SER_NULA)));
            certificado.setProvider(Optional.ofNullable(provider).orElseThrow(() -> new IllegalArgumentException("Provider não pode ser nulo.")));

            Enumeration<String> aliasEnum = getKeyStore(certificado).aliases();

            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();
                if (aliasKey !=
                        null) {
                    listaCert.add(aliasKey);
                }
            }

            return listaCert;
        } catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados A3:" +
                    ex.getMessage(), ex);
        }

    }

    private static Date dataValidade(X509Certificate certificate) {
        return Optional.ofNullable(certificate.getNotAfter())
                .orElse(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private static Long diasRestantes(Certificado certificado) {
        return LocalDate.now().until(certificado.getVencimento(), ChronoUnit.DAYS);
    }

    private static boolean valido(Certificado certificado) {
        return LocalDate.now().isBefore(certificado.getVencimento());
    }

    public static X509Certificate getCertificate(Certificado certificado, KeyStore keystore) throws CertificadoException {
        try {

            return (X509Certificate) keystore.getCertificate(certificado.getNome());

        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro Ao pegar X509Certificate: " +
                    e.getMessage(), e);
        }

    }

    public static KeyStore getKeyStore(Certificado certificado) throws CertificadoException {
        try {

            switch (certificado.getTipoCertificado()) {
                case REPOSITORIO_WINDOWS:
                    return KeyStoreService.getKeyStoreRepositorioWindows();
                case REPOSITORIO_MAC:
                    return KeyStoreService.getKeyStoreRepositorioMac();
                case ARQUIVO:
                    return KeyStoreService.getKeyStoreArquivo(certificado);
                case ARQUIVO_BYTES:
                    return KeyStoreService.getKeyStoreArquivoByte(certificado.getArquivoBytes(), certificado);
                case TOKEN_A3:
                    return KeyStoreService.getKeyStoreA3(certificado);
                default:
                    throw new CertificadoException("Tipo de certificado não Configurado: " +
                            certificado.getTipoCertificado());
            }
        } catch (Exception e) {
            if (Optional.ofNullable(e.getMessage()).orElse("").startsWith("keystore password was incorrect"))
                throw new CertificadoException("Senha do Certificado inválida.");

            throw new CertificadoException("Erro Ao pegar KeyStore: " +
                    e.getMessage(), e);
        }

    }

    public static Certificado getCertificadoByCnpjCpf(String cnpjCpf) throws CertificadoException {
        return listaCertificadosWindows().stream().filter(cert -> Optional.ofNullable(cert.getCnpjCpf()).orElse("")
                .startsWith(cnpjCpf)).findFirst().orElseThrow(() -> new CertificadoException(
                "Certificado não encontrado com CNPJ/CPF : " +
                        cnpjCpf));
    }

}
