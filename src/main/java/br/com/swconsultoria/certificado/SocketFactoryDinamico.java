/**
 *
 */
package br.com.swconsultoria.certificado;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

/**
 * Fábrica de sockets SSL/TLS utilizada pelo Axis2 (Apache Commons HttpClient 3.1)
 * para as conexões com os webservices da SEFAZ (NF-e, CT-e, MDF-e etc.).
 *
 * <p><b>Correção para eToken GD da Certsign via Repositório Windows (SunMSCAPI) no Java 21+</b></p>
 *
 * <p>Tokens A3 do tipo eToken GD (Certsign), quando acessados pelo Repositório de
 * Certificados do Windows ({@code REPOSITORIO_WINDOWS}), utilizam o provedor
 * {@code SunMSCAPI} para operações criptográficas. No Java 21, o TLS passou a
 * anunciar e priorizar esquemas de assinatura RSA-PSS ({@code rsa_pss_rsae_sha256} etc.)
 * durante o handshake TLS.</p>
 *
 * <p>O {@code SunMSCAPI} delega a assinatura à API CNG do Windows. Para tokens
 * eToken GD (e outros tokens SafeSign/similares), a CNG retorna o erro
 * {@code NTE_BAD_FLAGS (0x80090009)} quando invocada com os flags internos que o
 * Java usa para RSA-PSS, resultando em:</p>
 * <pre>
 *   SSLHandshakeException: (handshake_failure) Cannot produce CertificateVerify signature
 *   Caused by: SignatureException: error 2148073481, Sinalizadores inválidos especificados.
 * </pre>
 *
 * <p>A solução implementada restringe os esquemas de assinatura TLS aceitos pelo
 * socket SSL, excluindo todos os esquemas RSA-PSS e mantendo apenas PKCS#1 v1.5 e
 * ECDSA. A restrição é aplicada via {@code SSLParameters.setSignatureSchemes()}
 * (disponível desde o Java 13) usando reflexão para manter a compatibilidade de
 * compilação com Java 8. Quando o provedor não é {@code SunMSCAPI} (ex.: certificado
 * A1 em arquivo .pfx), o comportamento é idêntico ao original.</p>
 */
public class SocketFactoryDinamico implements ProtocolSocketFactory {

    private static final Logger log = Logger.getLogger(SocketFactoryDinamico.class.getName());
    private static final char[] SENHA_CACERT = "changeit".toCharArray();

    /**
     * Esquemas de assinatura TLS restritos para uso com o provedor SunMSCAPI.
     *
     * <p>O Java 21 inclui por padrão os esquemas RSA-PSS ({@code rsa_pss_rsae_sha256},
     * {@code rsa_pss_rsae_sha384}, {@code rsa_pss_rsae_sha512}) na lista de esquemas
     * suportados para o TLS CertificateVerify. Esses esquemas causam erro
     * {@code NTE_BAD_FLAGS} no CNG do Windows ao usar tokens eToken GD (Certsign) e
     * outros tokens compatíveis com SafeSign. Esta lista permite apenas esquemas
     * PKCS#1 v1.5 e ECDSA, que funcionam corretamente com esses tokens.</p>
     */
    private static final String[] PKCS1_ONLY_SCHEMES = {
        "rsa_pkcs1_sha256", "rsa_pkcs1_sha384", "rsa_pkcs1_sha512",
        "ecdsa_secp256r1_sha256", "ecdsa_secp384r1_sha384", "ecdsa_secp521r1_sha512"
    };

    private final KeyStore keyStore;
    private final String alias;
    private final String senha;
    private final InputStream fileCacerts;
    private final SSLContext ssl;

    /**
     * Indica se o KeyStore utilizado é gerenciado pelo provedor {@code SunMSCAPI}
     * (Repositório de Certificados do Windows). Quando {@code true}, o workaround
     * de restrição de esquemas de assinatura TLS é aplicado em cada socket criado.
     */
    private final boolean isMscapiKeyStore;

    public SocketFactoryDinamico(KeyStore keyStore, String alias, String senha, InputStream fileCacerts, String sslProtocol) throws KeyManagementException,
            CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException {
        this.keyStore = keyStore;
        this.alias = alias;
        this.senha = senha;
        this.fileCacerts = fileCacerts;
        this.ssl = createSSLContext(sslProtocol);
        this.isMscapiKeyStore = keyStore != null && "SunMSCAPI".equals(keyStore.getProvider().getName());
    }

    /**
     * Aplica o workaround para tokens eToken GD (Certsign) e similares via SunMSCAPI.
     *
     * <p>Quando o KeyStore é gerenciado pelo provedor {@code SunMSCAPI}, restringe os
     * esquemas de assinatura TLS do socket para apenas PKCS#1 v1.5 e ECDSA, evitando
     * o erro {@code NTE_BAD_FLAGS} do CNG do Windows ao tentar usar RSA-PSS.</p>
     *
     * <p>O método {@code SSLParameters.setSignatureSchemes()} foi introduzido no Java 13.
     * Para manter a compatibilidade de compilação com Java 8 ({@code source/target=1.8}),
     * a invocação é feita via reflexão. Em Java 8–12, o método não existe e o bloco
     * {@code catch(NoSuchMethodException)} silencia o erro sem prejudicar a conexão
     * (nesses JDKs os esquemas RSA-PSS não são oferecidos por padrão).</p>
     *
     * <p>A restrição é aplicada <em>antes</em> da chamada {@code connect()}, pois o
     * handshake TLS é lazy no Java — ocorre na primeira escrita, não na criação do socket.
     * Portanto, {@code setSSLParameters()} deve ser chamado antes do {@code connect()}
     * para que os esquemas restritos sejam usados no ClientHello.</p>
     *
     * @param socket socket recém-criado pelo SSLContext, antes do connect/handshake
     */
    private void applyMscapiWorkaround(Socket socket) {
        if (!isMscapiKeyStore || !(socket instanceof SSLSocket)) return;
        try {
            SSLSocket sslSocket = (SSLSocket) socket;
            SSLParameters params = sslSocket.getSSLParameters();
            Method setSignatureSchemes = SSLParameters.class.getMethod("setSignatureSchemes", String[].class);
            setSignatureSchemes.invoke(params, (Object) PKCS1_ONLY_SCHEMES);
            sslSocket.setSSLParameters(params);
        } catch (NoSuchMethodException ignored) {
            // Java < 13: SSLParameters.setSignatureSchemes() não existe — nenhuma ação necessária,
            // pois nesses JDKs o RSA-PSS não é incluído nos esquemas padrão do TLS.
        } catch (Exception e) {
            log.warning("[CERT] Falha ao aplicar workaround SunMSCAPI (eToken GD): " + e.getMessage());
        }
    }

    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort, final HttpConnectionParams params) throws IOException {
        final Socket socket = this.ssl.getSocketFactory().createSocket();
        applyMscapiWorkaround(socket);
        socket.bind(new InetSocketAddress(localAddress, localPort));
        socket.connect(new InetSocketAddress(host, port), 60000);
        return socket;
    }

    @Override
    public Socket createSocket(final String host, final int port, final InetAddress clientHost, final int clientPort) throws IOException {
        Socket socket = this.ssl.getSocketFactory().createSocket(host, port, clientHost, clientPort);
        applyMscapiWorkaround(socket);
        return socket;
    }

    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        Socket socket = this.ssl.getSocketFactory().createSocket(host, port);
        applyMscapiWorkaround(socket);
        return socket;
    }

    private SSLContext createSSLContext(String sslProtocol) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        final KeyManager[] keyManagers = createKeyManagers();
        final TrustManager[] trustManagers = createTrustManagers();
        final SSLContext sslContext = SSLContext.getInstance(sslProtocol);
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    public KeyManager[] createKeyManagers() {
        return new KeyManager[]{new AliasKeyManager(keyStore, alias, senha)};
    }

    public TrustManager[] createTrustManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(fileCacerts, SENHA_CACERT);
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }

    public SSLContext getSsl() {
        return ssl;
    }
}