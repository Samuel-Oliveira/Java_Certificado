package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import org.apache.commons.httpclient.protocol.Protocol;
import org.bouncycastle.asn1.*;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class CertificadoService {

    private static final DERObjectIdentifier CNPJ = new DERObjectIdentifier("2.16.76.1.3.3");
    private static final DERObjectIdentifier CPF = new DERObjectIdentifier("2.16.76.1.3.1");

    public static void inicializaCertificado(Certificado certificado, InputStream cacert) throws CertificadoException {

        try {

            KeyStore keyStore = getKeyStore(certificado);
            X509Certificate certificate = getCertificate(certificado, keyStore);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(certificado.getNome(), certificado.getSenha().toCharArray());
            if (certificado.isAtivarProperties()) {
                CertificadoProperties.inicia(certificado, cacert);
            } else {
                SocketFactoryDinamico socketFactory = new SocketFactoryDinamico(certificate, privateKey, cacert, certificado.getSslProtocol(), keyStore, certificado.getNome());
                Protocol protocol = new Protocol("https", socketFactory, 443);
                Protocol.registerProtocol("https", protocol);
            }

        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException | CertificateException | IOException e) {
            throw new CertificadoException(e.getMessage());
        }

    }

    public static Certificado certificadoPfxBytes(byte[] certificadoBytes, String senha) throws CertificadoException {

        Certificado certificado = new Certificado();
        try {
            certificado.setArquivoBytes(certificadoBytes);
            certificado.setSenha(senha);
            certificado.setTipo(Certificado.ARQUIVO_BYTES);

            KeyStore keyStore = getKeyStore(certificado);
            Enumeration<String> aliasEnum = keyStore.aliases();
            String aliasKey = aliasEnum.nextElement();

            certificado.setNome(aliasKey);
            certificado.setCnpjCpf(getDocumentoFromCertificado(certificado, keyStore));
            certificado.setVencimento(DataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            certificado.setDiasRestantes(diasRestantes(certificado));
            certificado.setValido(valido(certificado));
        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" + e.getMessage());
        }

        return certificado;

    }

    public static Certificado certificadoPfx(String caminhoCertificado, String senha) throws CertificadoException {

        Certificado certificado = new Certificado();
        try {
            certificado.setArquivo(caminhoCertificado);
            certificado.setSenha(senha);
            certificado.setTipo(Certificado.ARQUIVO);

            KeyStore keyStore = getKeyStore(certificado);
            Enumeration<String> aliasEnum = keyStore.aliases();
            String aliasKey = aliasEnum.nextElement();

            certificado.setNome(aliasKey);
            certificado.setCnpjCpf(getDocumentoFromCertificado(certificado, keyStore));
            certificado.setVencimento(DataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            certificado.setDiasRestantes(diasRestantes(certificado));
            certificado.setValido(valido(certificado));
        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" + e.getMessage());
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
            certificado.setTipo(Certificado.A3);
            certificado.setSerialToken(serialToken);

            KeyStore keyStore = getKeyStore(certificado);
            if (alias == null) {
                certificado.setNome(keyStore.aliases().nextElement());
            } else {
                certificado.setNome(alias);
            }

            certificado.setCnpjCpf(getDocumentoFromCertificado(certificado, keyStore));
            certificado.setVencimento(DataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            certificado.setDiasRestantes(diasRestantes(certificado));
            certificado.setValido(valido(certificado));

            return certificado;
        } catch (Exception e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" + e.getMessage());
        }

    }

    public static List<Certificado> listaCertificadosWindows() throws CertificadoException {

        List<Certificado> listaCert = new ArrayList<>();
        Certificado certificado = new Certificado();
        certificado.setTipo(Certificado.WINDOWS);
        try {
            KeyStore ks = getKeyStore(certificado);
            Enumeration<String> aliasEnum = ks.aliases();

            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();

                if (aliasKey != null) {
                    Certificado cert = new Certificado();
                    cert.setNome(aliasKey);
                    cert.setCnpjCpf(getDocumentoFromCertificado(cert, ks));
                    cert.setTipo(Certificado.WINDOWS);
                    cert.setSenha("");
                    Date dataValidade = DataValidade(cert);
                    if (dataValidade == null) {
                        cert.setNome("(INVALIDO)" + aliasKey);
                        cert.setVencimento(LocalDate.of(2000, 1, 1));
                        cert.setDiasRestantes(0L);
                        cert.setValido(false);
                    } else {
                        cert.setVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                        cert.setDiasRestantes(diasRestantes(cert));
                        cert.setValido(valido(cert));
                    }

                    listaCert.add(cert);
                }

            }

        } catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados:" + ex.getMessage());
        }

        return listaCert;

    }

    public static List<Certificado> listaCertificadosMac() throws CertificadoException {

        List<Certificado> listaCert = new ArrayList<>();
        Certificado certificado = new Certificado();
        certificado.setTipo(Certificado.MAC);
        try {
            KeyStore ks = getKeyStore(certificado);
            Enumeration<String> aliasEnum = ks.aliases();

            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();

                if (aliasKey != null) {
                    Certificado cert = new Certificado();
                    cert.setNome(aliasKey);
                    cert.setCnpjCpf(getDocumentoFromCertificado(cert, ks));
                    cert.setTipo(Certificado.MAC);
                    cert.setSenha("");
                    Date dataValidade = DataValidade(cert);
                    if (dataValidade == null) {
                        cert.setNome("(INVALIDO)" + aliasKey);
                        cert.setVencimento(LocalDate.of(2000, 1, 1));
                        cert.setDiasRestantes(0L);
                        cert.setValido(false);
                    } else {
                        cert.setVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                        cert.setDiasRestantes(diasRestantes(cert));
                        cert.setValido(valido(cert));
                    }

                    listaCert.add(cert);
                }

            }

        } catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados:" + ex.getMessage());
        }

        return listaCert;

    }

    public static List<String> listaAliasCertificadosA3(String marca, String dll, String senha) throws CertificadoException {

        try {
            List<String> listaCert = new ArrayList<>(20);
            Certificado certificado = new Certificado();
            certificado.setTipo(Certificado.A3);
            certificado.setMarcaA3(marca);
            certificado.setSenha(senha);
            certificado.setDllA3(dll);
            certificado.setTipo(Certificado.A3);

            Enumeration<String> aliasEnum = getKeyStore(certificado).aliases();

            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();
                if (aliasKey != null) {
                    listaCert.add(aliasKey);
                }
            }

            return listaCert;
        } catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados A3:" + ex.getMessage());
        }

    }

    private static Date DataValidade(Certificado certificado) throws CertificadoException {

        KeyStore keyStore = getKeyStore(certificado);
        if (keyStore == null) {
            throw new CertificadoException("Erro Ao pegar Keytore, verifique o Certificado");
        }

        X509Certificate certificate = getCertificate(certificado, keyStore);


        return certificate.getNotAfter();

    }

    private static Long diasRestantes(Certificado certificado) throws CertificadoException {

        Date data = DataValidade(certificado);
        if (data == null) {
            return null;
        }
        long differenceMilliSeconds = data.getTime() - new Date().getTime();
        return differenceMilliSeconds / 1000 / 60 / 60 / 24;
    }

    private static boolean valido(Certificado certificado) throws CertificadoException {

        return DataValidade(certificado) != null && DataValidade(certificado).after(new Date());

    }

    public static KeyStore getKeyStore(Certificado certificado) throws CertificadoException {
        try {
            KeyStore keyStore;

            switch (certificado.getTipo()) {
                case Certificado.WINDOWS:
                    keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
                    keyStore.load(null, null);
                    return keyStore;
                case Certificado.MAC:
                    keyStore = KeyStore.getInstance("KeychainStore");
                    keyStore.load(null, null);
                    return keyStore;
                case Certificado.ARQUIVO:
                    File file = new File(certificado.getArquivo());
                    if (!file.exists()) {
                        throw new CertificadoException("Certificado Digital não Encontrado");
                    }

                    keyStore = KeyStore.getInstance("PKCS12");
                    keyStore.load(new ByteArrayInputStream(getBytesFromInputStream(new FileInputStream(file))), certificado.getSenha().toCharArray());
                    return keyStore;
                case Certificado.ARQUIVO_BYTES:
                    keyStore = KeyStore.getInstance("PKCS12");
                    keyStore.load(new ByteArrayInputStream(certificado.getArquivoBytes()),
                            certificado.getSenha().toCharArray());
                    return keyStore;
                case Certificado.A3:
                    System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
                    String slot = null;
                    if (certificado.getSerialToken() != null) {
                        slot = getSlot(certificado.getDllA3(), certificado.getSerialToken());
                    }
                    InputStream conf = configA3(certificado.getMarcaA3(), certificado.getDllA3(), slot);
                    Provider p = new sun.security.pkcs11.SunPKCS11(conf);
                    Security.addProvider(p);

                    keyStore = KeyStore.getInstance("PKCS11");

                    if (keyStore.getProvider() == null) {
                        keyStore = KeyStore.getInstance("PKCS11", p);
                    }

                    keyStore.load(null, certificado.getSenha().toCharArray());
                    return keyStore;
                default:
                    return null;
            }
        } catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException | NoSuchProviderException e) {
            throw new CertificadoException("Erro Ao pegar KeyStore: " + e.getMessage());
        }

    }

    public static X509Certificate getCertificate(Certificado certificado, KeyStore keystore) throws CertificadoException {
        try {

            return (X509Certificate) keystore.getCertificate(certificado.getNome());

        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro Ao pegar X509Certificate: " + e.getMessage());
        }

    }

    private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1; )
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        }
    }

    private static InputStream configA3(String marca, String dll, String slot)
            throws UnsupportedEncodingException {

        String slotInfo = "";

        if (slot != null) {
            slotInfo = "\n\r" +
                    "slot = " + slot;
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
                throw new CertificadoException("Erro ao pegar Slot A3: " + e.getMessage());
            }
        }

        try {
            slotList = tmpPKCS11.C_GetSlotList(true);

            for (long slot : slotList) {
                CK_TOKEN_INFO tokenInfo = tmpPKCS11.C_GetTokenInfo(slot);
                if (serialNumber.equals(String.valueOf(tokenInfo.serialNumber))) {
                    slotSelected = String.valueOf(slot);
                }
            }
        } catch (Exception e) {
            throw new CertificadoException("Erro Ao pegar SlotA3: " + e.getMessage());
        }

        return slotSelected;
    }

    @Deprecated
    public static Certificado getCertificadoByCnpj(String cnpj) throws CertificadoException {
        return getCertificadoByCnpjCpf(cnpj);
    }

    @Deprecated
    public static Certificado getCertificadoByCpf(String cnpj) throws CertificadoException {
        return getCertificadoByCnpjCpf(cnpj);
    }

    public static Certificado getCertificadoByCnpjCpf(String cnpjCpf) throws CertificadoException {
        return listaCertificadosWindows().stream().filter(cert -> cnpjCpf.equals(cert.getCnpjCpf())).findFirst().orElseThrow(() -> new CertificadoException("Certificado não encontrado com CNPJ/CPF : " + cnpjCpf));
    }

    private static String getDocumentoFromCertificado(Certificado certificado, KeyStore keyStore) throws CertificadoException {

        final String[] cnpjCpf = {""};
        try {
            X509Certificate certificate = getCertificate(certificado, keyStore);

            verifica(certificate.getSubjectAlternativeNames()).orElse(new ArrayList<>())
                    .stream().filter(x -> x.get(0).equals(0)).forEach(a -> {
                try {
                    byte[] data = (byte[]) a.get(1);
                    ASN1InputStream is = new ASN1InputStream(data);
                    DERSequence derSequence = (DERSequence) is.readObject();
                    DERObjectIdentifier tipo = DERObjectIdentifier.getInstance(derSequence.getObjectAt(0));
                    if (CNPJ.equals(tipo) || CPF.equals(tipo)) {
                        Object objeto = ((DERTaggedObject) ((DERTaggedObject) derSequence.getObjectAt(1)).getObject()).getObject();
                        if (objeto instanceof DEROctetString) {
                            cnpjCpf[0] = new String(((DEROctetString) objeto).getOctets());
                        } else if (objeto instanceof DERPrintableString) {
                            cnpjCpf[0] = ((DERPrintableString) objeto).getString();
                        } else if (objeto instanceof DERUTF8String) {
                            cnpjCpf[0] = ((DERUTF8String) objeto).getString();
                        } else if (objeto instanceof DERIA5String) {
                            cnpjCpf[0] = ((DERIA5String) objeto).getString();
                        }
                    }
                    if(CPF.equals(tipo) && cnpjCpf[0].length() > 25){
                        cnpjCpf[0] = cnpjCpf[0].substring(8, 19);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new CertificadoException("Erro ao pegar Docuemnto do Certificado: " + e.getMessage());
        }
        return cnpjCpf[0];
    }

    /**
     * Verifica se um objeto é vazio.
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> Optional<T> verifica(T obj) {
        if (obj == null)
            return Optional.empty();
        if (obj instanceof Collection)
            return ((Collection<?>) obj).size() == 0 ? Optional.empty() : Optional.of(obj);

        final String s = String.valueOf(obj).trim();

        return s.length() == 0 || s.equalsIgnoreCase("null") ? Optional.empty() : Optional.of(obj);
    }
}