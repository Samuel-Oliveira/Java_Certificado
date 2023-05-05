/**
 *
 */
package br.com.swconsultoria.certificado;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

class SocketFactoryDinamico implements ProtocolSocketFactory {

    private final KeyStore keyStore;
    private final String alias;
    private final String senha;
    private final InputStream fileCacerts;
    private SSLContext ssl;

    SocketFactoryDinamico(KeyStore keyStore, String alias, String senha, InputStream fileCacerts, String sslProtocol) throws KeyManagementException,
            CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException {
        this.keyStore = keyStore;
        this.alias = alias;
        this.senha = senha;
        this.fileCacerts = fileCacerts;
        this.ssl = createSSLContext(sslProtocol);
    }

    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort, final HttpConnectionParams params) throws IOException {
        final Socket socket = this.ssl.getSocketFactory().createSocket();
        socket.bind(new InetSocketAddress(localAddress, localPort));
        socket.connect(new InetSocketAddress(host, port), 60000);
        return socket;
    }

    @Override
    public Socket createSocket(final String host, final int port, final InetAddress clientHost, final int clientPort) throws IOException {
        return this.ssl.getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }

    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        return this.ssl.getSocketFactory().createSocket(host, port);
    }

    private SSLContext createSSLContext(String sslProtocol) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        final KeyManager[] keyManagers = createKeyManagers();
        final TrustManager[] trustManagers = createTrustManagers();
        final SSLContext sslContext = SSLContext.getInstance(sslProtocol);
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private KeyManager[] createKeyManagers() {
        return new KeyManager[]{new AliasKeyManager(keyStore, alias, senha)};
    }

    private TrustManager[] createTrustManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(fileCacerts, "changeit".toCharArray());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }
}