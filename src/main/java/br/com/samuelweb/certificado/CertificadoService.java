package br.com.samuelweb.certificado;

import br.com.samuelweb.certificado.exception.CertificadoException;
import org.apache.commons.httpclient.protocol.Protocol;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Classe Responsavel Por Carregar os Certificados Do Repositorio do Windows
 *
 * @author SaMuK
 */
public class CertificadoService {

    /**
     * Metodo Que Inicializa as Informações de Certificado Digital
     *
     * @param certificado
     * @param cacert
     * @throws CertificadoException
     */
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

    /**
     * Metodo Que retorna um Certificado do Tipo PFX Bytes
     *
     * @param certificadoBytes
     * @param senha
     * @return
     * @throws CertificadoException
     */
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
            certificado
                    .setVencimento(DataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            certificado.setDiasRestantes(diasRestantes(certificado));
            certificado.setValido(valido(certificado));
        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" + e.getMessage());
        }

        return certificado;

    }

    /**
     * Metodo Que retorna um Certificado do Tipo PFX
     *
     * @param caminhoCertificado
     * @param senha
     * @return
     * @throws CertificadoException
     */
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
            certificado.setVencimento(DataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            certificado.setDiasRestantes(diasRestantes(certificado));
            certificado.setValido(valido(certificado));
        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" + e.getMessage());
        }

        return certificado;

    }

    /**
     * Metodo Que retorna um Certificado do Tipo A3
     *
     * @param marca
     * @param dll
     * @param senha
     * @return
     * @throws CertificadoException
     */
    public static Certificado certificadoA3(String marca, String dll, String senha) throws CertificadoException {

        Certificado certificado = new Certificado();
        try {
            certificado.setMarcaA3(marca);
            certificado.setSenha(senha);
            certificado.setDllA3(dll);
            certificado.setTipo(Certificado.A3);

            Enumeration<String> aliasEnum = getKeyStore(certificado).aliases();

            certificado.setNome(aliasEnum.nextElement());
            certificado.setVencimento(DataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            certificado.setDiasRestantes(diasRestantes(certificado));
            certificado.setValido(valido(certificado));
        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informações do certificado:" + e.getMessage());
        }

        return certificado;

    }

    /**
     * Metodo Que retorna um Certificado do Tipo A3 passando a Alias, para mais de 1 certificado A3.
     *
     * @param marca
     * @param dll
     * @param senha
     * @param Alias
     * @return
     * @throws CertificadoException
     */
    public static Certificado certificadoA3(String marca, String dll, String senha, String alias) throws CertificadoException {

        Certificado certificado = new Certificado();
        certificado.setMarcaA3(marca);
        certificado.setSenha(senha);
        certificado.setDllA3(dll);
        certificado.setTipo(Certificado.A3);

        certificado.setNome(alias);
        certificado.setVencimento(DataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        certificado.setDiasRestantes(diasRestantes(certificado));
        certificado.setValido(valido(certificado));

        return certificado;

    }

    /**
     * Metodo Que retorna um Certificado do Tipo A3 passando a Alias e Serial do Token/SmartCard. Utilizado quando se possui mais de um SmartCard/Token USB conectado.
     *
     * @param marca
     * @param dll
     * @param senha
     * @param Alias
     * @return
     * @throws CertificadoException
     */
    public static Certificado certificadoA3(String marca, String dll, String senha, String alias, String serialToken) throws CertificadoException {

        Certificado certificado = new Certificado();
        certificado.setMarcaA3(marca);
        certificado.setSenha(senha);
        certificado.setDllA3(dll);
        certificado.setTipo(Certificado.A3);
        certificado.setSerialToken(serialToken);
        certificado.setNome(alias);
        certificado.setVencimento(DataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        certificado.setDiasRestantes(diasRestantes(certificado));
        certificado.setValido(valido(certificado));


        return certificado;

    }

    /**
     * Retorna a Lista De Certificados Do Repositorio Do Windows
     *
     * @return
     * @throws CertificadoException
     */
    public static List<Certificado> listaCertificadosWindows() throws CertificadoException {

        // Estou setando a variavel para 20 dispositivos no maximo
        List<Certificado> listaCert = new ArrayList<>(20);
        Certificado certificado = new Certificado();
        certificado.setTipo(Certificado.WINDOWS);
        ;
        try {
            KeyStore ks = getKeyStore(certificado);
            Enumeration<String> aliasEnum = ks.aliases();

            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();

                if (aliasKey != null) {
                    Certificado cert = new Certificado();
                    cert.setNome(aliasKey);
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

    /**
     * Retorna a Lista De Certificados A3
     *
     * @return
     * @throws CertificadoException
     */
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

    /**
     * Método  que retorna a Data De Validade Do Certificado Digital
     *
     * @param certificado
     * @return
     * @throws CertificadoException
     */
    private static Date DataValidade(Certificado certificado) throws CertificadoException {

        KeyStore keyStore = getKeyStore(certificado);
        if (keyStore == null) {
            throw new CertificadoException("Erro Ao pegar Keytore, verifique o Certificado");
        }

        X509Certificate certificate = getCertificate(certificado, keyStore);


        return certificate.getNotAfter();

    }


    /**
     * Método que retorna os dias Restantes do Certificado Digital
     *
     * @param certificado
     * @return
     * @throws CertificadoException
     */
    private static Long diasRestantes(Certificado certificado) throws CertificadoException {

        Date data = DataValidade(certificado);
        if (data == null) {
            return null;
        }
        long differenceMilliSeconds = data.getTime() - new Date().getTime();
        return differenceMilliSeconds / 1000 / 60 / 60 / 24;
    }

    /**
     * Método Que retorno se o Certificado é válido
     *
     * @param certificado
     * @return
     * @throws CertificadoException
     */
    private static boolean valido(Certificado certificado) throws CertificadoException {

        return DataValidade(certificado) != null && DataValidade(certificado).after(new Date());

    }

    /**
     * Retorna a KeyStore do Certificado
     *
     * @param certificado
     * @return
     * @throws CertificadoException
     */
    public static KeyStore getKeyStore(Certificado certificado) throws CertificadoException {
        try {
            KeyStore keyStore;

            switch (certificado.getTipo()) {
                case Certificado.WINDOWS:
                    keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
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

    /**
     * Retorna o X509Certificate do Certificado
     *
     * @param certificado
     * @param keystore
     * @return
     * @throws CertificadoException
     */
    public static X509Certificate getCertificate(Certificado certificado, KeyStore keystore) throws CertificadoException {
        try {

            return (X509Certificate) keystore.getCertificate(certificado.getNome());

        } catch (KeyStoreException e) {
            throw new CertificadoException("Erro Ao pegar X509Certificate: " + e.getMessage());
        }

    }

    /**
     * Metodo Para Retornar o Byte[] do InputStream
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1; )
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        }
    }

    /**
     * Metodo que Retorna o InputStream das Configurações do Certificado A3
     *
     * @param marca
     * @param dll
     * @return
     * @throws UnsupportedEncodingException
     */
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

}