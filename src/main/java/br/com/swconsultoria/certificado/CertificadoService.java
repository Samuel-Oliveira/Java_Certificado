package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import org.apache.commons.httpclient.protocol.Protocol;
import org.bouncycastle.asn1.*;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class CertificadoService {

    private static final ASN1ObjectIdentifier CNPJ = new ASN1ObjectIdentifier("2.16.76.1.3.3");
    private static final ASN1ObjectIdentifier CPF = new ASN1ObjectIdentifier("2.16.76.1.3.1");
    private static boolean cacertProprio;
    private static String ultimoLog = "";

    public static void inicializaCertificado(Certificado certificado) throws CertificadoException {
        cacertProprio = true;
        inicializaCertificado(certificado, CertificadoService.class.getResourceAsStream("/cacert"));
    }

    public static void inicializaCertificado(Certificado certificado, InputStream cacert) throws CertificadoException {

        Optional.ofNullable(certificado).orElseThrow(() -> new IllegalArgumentException("Certificado não pode ser nulo."));
        Optional.ofNullable(cacert).orElseThrow(() -> new IllegalArgumentException("Cacert não pode ser nulo."));

        try {

            KeyStore keyStore = getKeyStore(certificado);
            if (certificado.isAtivarProperties()) {
                CertificadoProperties.inicia(certificado, cacert);
            } else {
                SocketFactoryDinamico socketFactory = new SocketFactoryDinamico(keyStore, certificado.getNome(), certificado.getSenha(), cacert,
                        certificado.getSslProtocol());
                Protocol protocol = new Protocol("https", socketFactory, 443);
                Protocol.registerProtocol("https", protocol);
            }

            if (Logger.getLogger("").isLoggable(Level.SEVERE) && !ultimoLog.equals(certificado.getCnpjCpf())) {
                System.err.println("####################################################################");
                System.err.println("              Java-Certificado - Versão 2.10 - 05/05/2023            ");
                if (Logger.getLogger("").isLoggable(Level.WARNING)) {
                    System.err.println(" Samuel Olivera - samuel@swconsultoria.com.br ");
                }
                System.err.println(" Tipo: " + certificado.getTipoCertificado().toString() +
                        " - Vencimento: " + certificado.getDataHoraVencimento());
                if (certificado.getTipoCertificado().equals(TipoCertificadoEnum.ARQUIVO)) {
                    System.err.println(" Caminho: " + certificado.getArquivo());
                }
                System.err.println(" Cnpj/Cpf: " + certificado.getCnpjCpf() +
                        " - Alias: " + certificado.getNome().toUpperCase());
                System.err.println(" Arquivo Cacert: " + (cacertProprio ? "Default - Última Atualização: 05/05/2023" : "Customizado"));
                System.err.println(" Conexão SSL: " + (certificado.isAtivarProperties() ? "Properties (Não Recomendado)" : "Socket Dinãmico") +
                        " - Protocolo SSL: " + certificado.getSslProtocol());
                System.err.println("####################################################################");
                ultimoLog = certificado.getCnpjCpf();
            }

        } catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException | CertificateException | IOException e) {
            throw new CertificadoException(e.getMessage(),e);
        }

    }

    public static Certificado certificadoPfxBytes(byte[] certificadoBytes, String senha) throws CertificadoException {

        Certificado certificado = new Certificado();
        try {
            certificado.setArquivoBytes(Optional.ofNullable(certificadoBytes).orElseThrow(() -> new IllegalArgumentException("Certificado não pode ser nulo.")));
            certificado.setSenha(Optional.ofNullable(senha).orElseThrow(() -> new IllegalArgumentException("Senha não pode ser nula.")));
            certificado.setTipoCertificado(TipoCertificadoEnum.ARQUIVO_BYTES);
            setDadosCertificado(certificado, null);
        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" +
                    e.getMessage(),e);
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
        certificado.setCnpjCpf(getDocumentoFromCertificado(certificate));
        Date dataValidade = dataValidade(certificate);
        certificado.setVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        certificado.setDataHoraVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        certificado.setDiasRestantes(diasRestantes(certificado));
        certificado.setValido(valido(certificado));
        certificado.setNumeroSerie(certificate.getSerialNumber());
    }

    public static Certificado certificadoPfx(String caminhoCertificado, String senha) throws CertificadoException, FileNotFoundException {

        Optional.ofNullable(caminhoCertificado).orElseThrow(() -> new IllegalArgumentException("Caminho do Certificado não pode ser nulo."));
        Optional.ofNullable(senha).orElseThrow(() -> new IllegalArgumentException("Senha não pode ser nula."));

        if (!Files.exists(Paths.get(caminhoCertificado)))
            throw new FileNotFoundException("Arquivo " +
                    caminhoCertificado +
                    " não existe");

        Certificado certificado = new Certificado();

        try {
            certificado.setArquivo(caminhoCertificado);
            certificado.setSenha(senha);
            certificado.setTipoCertificado(TipoCertificadoEnum.ARQUIVO);
            setDadosCertificado(certificado, null);
        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" +
                    e.getMessage(),e);
        }

        return certificado;
    }

    public static Certificado certificadoA3(String marca, String dll, String senha) throws CertificadoException {
        return certificadoA3(marca, dll, senha, null, null);
    }

    public static Certificado certificadoA3(String marca, String dll, String senha, String alias) throws CertificadoException {

        return certificadoA3(marca, dll, senha, alias, null);

    }

    public static Certificado certificadoA3(String marca, String dll, String senha, String alias, String serialToken) throws CertificadoException {

        try {
            Certificado certificado = new Certificado();
            certificado.setMarcaA3(marca);
            certificado.setSenha(senha);
            certificado.setDllA3(dll);
            certificado.setTipoCertificado(TipoCertificadoEnum.TOKEN_A3);
            certificado.setSerialToken(serialToken);
            setDadosCertificado(certificado, null);
            return certificado;
        } catch (Exception e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" +
                    e.getMessage(),e);
        }

    }

    public static List<Certificado> listaCertificadosWindows() throws CertificadoException {
        return listaCertificadosRepositorio(TipoCertificadoEnum.REPOSITORIO_WINDOWS);
    }

    public static List<Certificado> listaCertificadosMac() throws CertificadoException {
        return listaCertificadosRepositorio(TipoCertificadoEnum.REPOSITORIO_MAC);
    }

    private static List<Certificado> listaCertificadosRepositorio(TipoCertificadoEnum tipo) throws CertificadoException {

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
                    listaCert.add(certificado);
                }
            }
        } catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados:" +
                    ex.getMessage(),ex);
        }
        return listaCert;
    }

    public static List<String> listaAliasCertificadosA3(String marca, String dll, String senha) throws CertificadoException {

        try {
            List<String> listaCert = new ArrayList<>(20);
            Certificado certificado = new Certificado();
            certificado.setTipoCertificado(TipoCertificadoEnum.TOKEN_A3);
            certificado.setMarcaA3(marca);
            certificado.setSenha(senha);
            certificado.setDllA3(dll);

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
                    ex.getMessage(),ex);
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

    public static KeyStore getKeyStore(Certificado certificado) throws CertificadoException {
        try {
            KeyStore keyStore;

            switch (certificado.getTipoCertificado()) {
                case REPOSITORIO_WINDOWS:
                    keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
                    keyStore.load(null, null);
                    return keyStore;
                case REPOSITORIO_MAC:
                    keyStore = KeyStore.getInstance("KeychainStore");
                    keyStore.load(null, null);
                    return keyStore;
                case ARQUIVO:
                    File file = new File(certificado.getArquivo());
                    if (!file.exists()) {
                        throw new CertificadoException("Certificado Digital não Encontrado");
                    }
                    keyStore = KeyStore.getInstance("PKCS12");
                    try (ByteArrayInputStream bs = new ByteArrayInputStream(Files.readAllBytes(file.toPath()))) {
                        keyStore.load(bs, certificado.getSenha().toCharArray());
                    }
                    return keyStore;
                case ARQUIVO_BYTES:
                    keyStore = KeyStore.getInstance("PKCS12");
                    try (ByteArrayInputStream bs = new ByteArrayInputStream(certificado.getArquivoBytes())) {
                        keyStore.load(bs, certificado.getSenha().toCharArray());
                    }
                    return keyStore;
                case TOKEN_A3:
                    System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
                    String slot = null;
                    if (certificado.getSerialToken() !=
                            null) {
                        slot = getSlot(certificado.getDllA3(), certificado.getSerialToken());
                    }
                    try (InputStream conf = configA3(certificado.getMarcaA3(), certificado.getDllA3(), slot)) {
                        Provider p = new sun.security.pkcs11.SunPKCS11(conf);
                        Security.addProvider(p);
                        keyStore = KeyStore.getInstance("PKCS11");
                        if (keyStore.getProvider() ==
                                null) {
                            keyStore = KeyStore.getInstance("PKCS11", p);
                        }
                        keyStore.load(null, certificado.getSenha().toCharArray());
                    }
                    return keyStore;
                default:
                    throw new CertificadoException("Tipo de certificado não Configurado: " +
                            certificado.getTipoCertificado());
            }
        } catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException | NoSuchProviderException e) {
            if (Optional.ofNullable(e.getMessage()).orElse("").startsWith("keystore password was incorrect"))
                throw new CertificadoException("Senha do Certificado inválida.");

            throw new CertificadoException("Erro Ao pegar KeyStore: " +
                    e.getMessage(),e);
        }

    }

    public static X509Certificate getCertificate(Certificado certificado, KeyStore keystore) throws CertificadoException {
        try {

            return (X509Certificate) keystore.getCertificate(certificado.getNome());

        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro Ao pegar X509Certificate: " +
                    e.getMessage(),e);
        }

    }

    private static InputStream configA3(String marca, String dll, String slot)
            throws UnsupportedEncodingException {

        String slotInfo = "";

        if (slot !=
                null) {
            slotInfo = "\n\r" +
                    "slot = " +
                    slot;
        }

        String conf = "name = " +
                marca +
                "\n\r" +
                "library = " +
                dll +
                slotInfo +
                "\n\r" +
                "showInfo = true";
        return new ByteArrayInputStream(conf.getBytes("UTF-8"));
    }

    private static String getSlot(String libraryPath, String serialNumber) throws IOException, CertificadoException {
        CK_C_INITIALIZE_ARGS initArgs = new CK_C_INITIALIZE_ARGS();
        String functionList = "C_GetFunctionList";

        initArgs.flags = 0;
        PKCS11 tmpPKCS11;
        long[] slotList;
        String slotSelected = null;
        try {
            try {
                tmpPKCS11 = PKCS11.getInstance(libraryPath, functionList, initArgs, false);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw ex;
            }
        } catch (PKCS11Exception e) {
            try {
                tmpPKCS11 = PKCS11.getInstance(libraryPath, functionList, null, true);
            } catch (Exception ex) {
                throw new CertificadoException("Erro ao pegar Slot A3: " +
                        e.getMessage(),e);
            }
        }

        try {
            slotList = tmpPKCS11.C_GetSlotList(true);

            for (long slot : slotList) {
                CK_TOKEN_INFO tokenInfo = tmpPKCS11.C_GetTokenInfo(slot);
                System.out.println("SLOTS: "+slot);
                System.out.println("SN: "+serialNumber);
                if (serialNumber.equals(String.valueOf(tokenInfo.serialNumber))) {
                    slotSelected = String.valueOf(slot);
                }
            }
        } catch (Exception e) {
            throw new CertificadoException("Erro Ao pegar SlotA3: " +
                    e.getMessage(),e);
        }

        return slotSelected;
    }

    public static Certificado getCertificadoByCnpjCpf(String cnpjCpf) throws CertificadoException {
        return listaCertificadosWindows().stream().filter(cert -> Optional.ofNullable(cert.getCnpjCpf()).orElse("")
                .startsWith(cnpjCpf)).findFirst().orElseThrow(() -> new CertificadoException(
                "Certificado não encontrado com CNPJ/CPF : " +
                cnpjCpf));
    }

    private static String getDocumentoFromCertificado(X509Certificate certificate) throws CertificadoException {

        final String[] cnpjCpf = {""};
        try {
            Optional.ofNullable(certificate.getSubjectAlternativeNames())
                    .ifPresent(lista ->
                            lista.stream().filter(x -> x.get(0).equals(0)).forEach(a -> {
                                byte[] data = (byte[]) a.get(1);
                                try (ASN1InputStream is = new ASN1InputStream(data)) {

                                    ASN1Sequence derSequence = (ASN1Sequence) is.readObject();
                                    ASN1ObjectIdentifier tipo = ASN1ObjectIdentifier.getInstance(derSequence.getObjectAt(0));
                                    if (CNPJ.equals(tipo) ||
                                            CPF.equals(tipo)) {
                                        Object objeto = ((ASN1TaggedObject) ((ASN1TaggedObject) derSequence.getObjectAt(1)).getObject()).getObject();
                                        if (objeto instanceof ASN1OctetString) {
                                            cnpjCpf[0] = new String(((ASN1OctetString) objeto).getOctets());
                                        } else if (objeto instanceof ASN1PrintableString) {
                                            cnpjCpf[0] = ((ASN1PrintableString) objeto).getString();
                                        } else if (objeto instanceof ASN1UTF8String) {
                                            cnpjCpf[0] = ((ASN1UTF8String) objeto).getString();
                                        } else if (objeto instanceof ASN1IA5String) {
                                            cnpjCpf[0] = ((ASN1IA5String) objeto).getString();
                                        }
                                    }
                                    if (CPF.equals(tipo) &&
                                            cnpjCpf[0].length() >
                                                    25) {
                                        cnpjCpf[0] = cnpjCpf[0].substring(8, 19);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }));

        } catch (Exception e) {
            throw new CertificadoException("Erro ao pegar Documento do Certificado: " +
                    e.getMessage(),e);
        }
        return cnpjCpf[0];
    }

}