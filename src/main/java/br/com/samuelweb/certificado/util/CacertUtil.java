/**
 * 
 */
package br.com.samuelweb.certificado.util;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Samuel Oliveira
 *
 */
public class CacertUtil {

	private static final int TIMEOUT_WS = 30;
	private static final int PORTA = 443;
	private static final String CACERT = "d:/java/util/Cacert/Cacert-04-07-2017";
	private static String cacert;

	public static void main(String[] args) {
		gerarCacert(null, CACERT);
	}

	/**
	 * Metodo que gerar o arquivo Cacert com a lista de WebServices Enviada
	 * Informe null na Lista De Endereços para usar a listagem Padrão
	 * 
	 * @param listaEnderecos
	 * @param caminhoCacert
	 */
	public static void gerarCacert(List<String> listaEnderecos, String caminhoCacert) {
		cacert = caminhoCacert;
		try {

			// Se não For informado Nenhuma LIsta, carrega a padrão
			if (listaEnderecos == null || listaEnderecos.isEmpty()) {
				listaEnderecos = listaPadraoWebService();
			}

			char[] senha = "changeit".toCharArray();
			File arquivoCacert = new File(cacert);

			if (arquivoCacert.isFile()) {
				arquivoCacert.delete();
			} 
			
			if(!arquivoCacert.isFile()) {
				File dir = new File(System.getProperty("java.home") + File.separatorChar + "lib" + File.separatorChar + "security");
				arquivoCacert = new File(dir, "cacerts");
			}

			info("| Carregando KeyStore " + arquivoCacert + "...");
			InputStream in = new FileInputStream(arquivoCacert);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, senha);
			in.close();

			listaEnderecos.stream().forEach(endereco -> {
				get(endereco, ks);
			});

			OutputStream out = new FileOutputStream(cacert);
			ks.store(out, senha);
			out.close();

			info("| Arquivo gerado com sucesso em " + cacert);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void get(String host, KeyStore ks) {
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
			SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
			context.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory factory = context.getSocketFactory();

			info("| Abrindo conexão com " + host + ":443...");
			SSLSocket socket = (SSLSocket) factory.createSocket(host, PORTA);
			socket.setSoTimeout(TIMEOUT_WS * 1000);
			try {
				info("| Iniciando SSL handshake...");
				socket.startHandshake();
				socket.close();
				info("| Sem erros, certificado adicionado");
			} catch (SSLHandshakeException e) {
				/**
				 * PKIX path building failed:
				 * sun.security.provider.certpath.SunCertPathBuilderException:
				 * Não tratado, pois sempre ocorre essa exception quando o
				 * cacert nao esta gerado.
				 */
			} catch (SSLException e) {
				error("| " + e.toString());
			}

			X509Certificate[] chain = tm.chain;
			if (chain == null) {
				info("| Não pode obter cadeia de certificados");
			} else {
				info("| Servidor enviou " + chain.length + " certificado(s):");
				MessageDigest sha1 = MessageDigest.getInstance("SHA1");
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				for (int i = 0; i < chain.length; i++) {
					X509Certificate cert = chain[i];
					sha1.update(cert.getEncoded());
					md5.update(cert.getEncoded());

					String alias = host + "-" + (i);
					ks.setCertificateEntry(alias, cert);
					info("| Adicionou certificado para o keystore '" + cacert + "' usando alias '" + alias + "'");
				}
			}
		} catch (NoSuchAlgorithmException | KeyStoreException | CertificateEncodingException | KeyManagementException | IOException e) {
			error("| " + e.toString());
		}
	}

	private static class SavingTrustManager implements X509TrustManager {
		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.chain = chain;
			this.tm.checkServerTrusted(chain, authType);
		}
	}

	private static void info(String log) {
		System.out.println("INFO: " + log);
	}

	private static void error(String log) {
		System.out.println("ERROR: " + log);
	}

	public static List<String> listaPadraoWebService() {
		List<String> listaWebServices = new ArrayList<>();
		// NFE HOMOLOGACAO
		listaWebServices.add("homnfe.sefaz.am.gov.br");
		listaWebServices.add("hnfe.sefaz.ba.gov.br");
		listaWebServices.add("nfeh.sefaz.ce.gov.br");
		listaWebServices.add("app.sefaz.es.gov.br");
		listaWebServices.add("homolog.sefaz.go.gov.br");
		listaWebServices.add("hnfe.fazenda.mg.gov.br");
		listaWebServices.add("homologacao.nfe.ms.gov.br");
		listaWebServices.add("homologacao.sefaz.mt.gov.br");
		listaWebServices.add("nfehomolog.sefaz.pe.gov.br");
		listaWebServices.add("homologacao.nfe.fazenda.pr.gov.br");
		listaWebServices.add("nfe-homologacao.sefazrs.rs.gov.br");
		listaWebServices.add("cad.sefazrs.rs.gov.br");
		listaWebServices.add("homologacao.nfe.fazenda.sp.gov.br");
		listaWebServices.add("hom.sefazvirtual.fazenda.gov.br");
		listaWebServices.add("nfe-homologacao.svrs.rs.gov.br");
		listaWebServices.add("cad.svrs.rs.gov.br");
		listaWebServices.add("hom.svc.fazenda.gov.br");
		listaWebServices.add("hom.nfe.fazenda.gov.br");

		// NFE PRODUCAO
		listaWebServices.add("nfe.sefaz.am.gov.br");
		listaWebServices.add("nfe.sefaz.ba.gov.br");
		listaWebServices.add("nfe.sefaz.ce.gov.br");
		listaWebServices.add("nfe.sefaz.go.gov.br");
		listaWebServices.add("nfe.fazenda.mg.gov.br");
		listaWebServices.add("nfe.fazenda.ms.gov.br");
		listaWebServices.add("nfe.sefaz.mt.gov.br");
		listaWebServices.add("nfe.sefaz.pe.gov.br");
		listaWebServices.add("nfe.fazenda.pr.gov.br");
		listaWebServices.add("nfe.sefazrs.rs.gov.br");
		listaWebServices.add("nfe.fazenda.sp.gov.br");
		listaWebServices.add("www.sefazvirtual.fazenda.gov.br");
		listaWebServices.add("nfe.svrs.rs.gov.br");
		listaWebServices.add("www.svc.fazenda.gov.br");
		listaWebServices.add("www.nfe.fazenda.gov.br");

		// NFCE HOMOLOGACAO
		listaWebServices.add("homnfce.sefaz.am.gov.br");
		listaWebServices.add("nfceh.sefaz.ce.gov.br");
		listaWebServices.add("homologacao.nfce.fazenda.ms.gov.br");
		listaWebServices.add("nfcehomolog.sefaz.pe.gov.br");
		listaWebServices.add("homologacao.nfce.fazenda.pr.gov.br");
		listaWebServices.add("nfce-homologacao.sefazrs.rs.gov.br");
		listaWebServices.add("homologacao.nfce.fazenda.sp.gov.br");
		listaWebServices.add("nfce-homologacao.svrs.rs.gov.br");

		// NFCE PRODUCAO
		listaWebServices.add("nfce.sefaz.am.gov.br");
		listaWebServices.add("nfce.fazenda.ms.gov.br");
		listaWebServices.add("nfce.sefaz.pe.gov.br");
		listaWebServices.add("nfce.fazenda.pr.gov.br");
		listaWebServices.add("nfce.sefazrs.rs.gov.br");
		listaWebServices.add("nfce.fazenda.sp.gov.br");
		listaWebServices.add("nfce.svrs.rs.gov.br");

		// CTE HOMOLOGACAO
		listaWebServices.add("hcte.fazenda.mg.gov.br");
		listaWebServices.add("homologacao.cte.ms.gov.br");
		listaWebServices.add("homologacao.cte.fazenda.pr.gov.br");
		listaWebServices.add("cte-homologacao.svrs.rs.gov.br");
		listaWebServices.add("hom1.cte.fazenda.gov.br");

		// CTE PRODUCAO
		listaWebServices.add("cte.fazenda.mg.gov.br");
		listaWebServices.add("producao.cte.ms.gov.br");
		listaWebServices.add("cte.sefaz.mt.gov.br");
		listaWebServices.add("cte.fazenda.pr.gov.br");
		listaWebServices.add("cte.svrs.rs.gov.br");
		listaWebServices.add("www1.cte.fazenda.gov.br");
		
		//MDFE HOMOLOGACAO
		listaWebServices.add("mdfe-homologacao.svrs.rs.gov.br");
		
		//MDFE PRODUCAO
		listaWebServices.add("mdfe.svrs.rs.gov.br");

		return listaWebServices;
	}
}
