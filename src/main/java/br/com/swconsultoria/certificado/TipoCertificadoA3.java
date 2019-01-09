package br.com.swconsultoria.certificado;

/**
 * Enum com os possiveis tipos de Certificados A3
 * 
 * @author Samuel Oliveira
 * 
 */
public enum TipoCertificadoA3 {
	LEITOR_SCR3310("SafeWeb","c:/windows/system32/cmp11.dll"),
	TOKEN_ALADDIN("eToken","c:/windows/system32/eTpkcs11.dll"),
	LEITOR_GEMPC_PERTO("SmartCard","c:/windows/system32/aetpkss1.dll"),
	OBERTHUR("Oberthur", "c:/windows/system32/OcsCryptoki.dll");

	private final String marca;
	private final String dll;

    TipoCertificadoA3(final String marca, final String dll) {
		this.marca = marca;
		this.dll = dll;
	}

	/**
	 * @return the Descricao
	 */
	public String getMarca() {
		return marca;
	}

	/**
	 * @return the Dll
	 */
	public String getDll() {
		return dll;
	}
	
}
