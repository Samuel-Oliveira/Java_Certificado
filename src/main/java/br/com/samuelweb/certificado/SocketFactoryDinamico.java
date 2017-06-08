/**
 * 
 */
package br.com.samuelweb.certificado;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

class SocketFactoryDinamico implements ProtocolSocketFactory {

	private SSLContext ssl = null;
	private X509Certificate certificate;
	private PrivateKey privateKey;
	private InputStream fileCacerts;

	public SocketFactoryDinamico(X509Certificate certificate, PrivateKey privateKey, InputStream fileCacerts) throws UnrecoverableKeyException, KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
		this.certificate = certificate;
		this.privateKey = privateKey;
		this.fileCacerts = fileCacerts;
		this.ssl = createSSLContext();
	}

    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort, final HttpConnectionParams params) throws IOException {
        final Socket socket = this.ssl.getSocketFactory().createSocket();
        ((SSLSocket) socket).setEnabledProtocols(new String[]{"SSLv3","TLSv1"});
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

    private SSLContext createSSLContext() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        final KeyManager[] keyManagers = createKeyManagers();
        final TrustManager[] trustManagers = createTrustManagers();
        final SSLContext sslContext = SSLContext.getInstance("TLSv1");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private KeyManager[] createKeyManagers() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        return new KeyManager[]{new NFKeyManager(certificate, privateKey)};
    }

    private TrustManager[] createTrustManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(fileCacerts, "changeit".toCharArray());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }
    
    private class NFKeyManager implements X509KeyManager {
        private final X509Certificate certificate;
        private final PrivateKey privateKey;
        
        NFKeyManager(final X509Certificate certificate, final PrivateKey privateKey) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }
        
        @Override
        public String chooseClientAlias(final String[] arg0, final Principal[] arg1, final Socket arg2) {
            return this.certificate.getIssuerDN().getName();
        }
        
        @Override
        public String chooseServerAlias(final String arg0, final Principal[] arg1, final Socket arg2) {
            return null;
        }
        
        @Override
        public X509Certificate[] getCertificateChain(final String arg0) {
            return new X509Certificate[]{this.certificate};
        }
        
        @Override
        public String[] getClientAliases(final String arg0, final Principal[] arg1) {
            return new String[]{this.certificate.getIssuerDN().getName()};
        }
        
        @Override
        public PrivateKey getPrivateKey(final String arg0) {
            return this.privateKey;
        }
        
        @Override
        public String[] getServerAliases(final String arg0, final Principal[] arg1) {
            return null;
        }
    }
}