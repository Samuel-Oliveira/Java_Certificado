package br.com.swconsultoria.certificado.exception;

/**
 * Exceção a ser lançada na ocorrência de falhas provenientes do Certificado.
 *
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.swconsultoria.com.br
 */
@SuppressWarnings("WeakerAccess")
public class CertificadoException extends Exception {

	private String message;
	
	/**
	 * Construtor da classe.
	 * 
	 * @param e
	 */
	public CertificadoException(Throwable e) {
		super(e);
	}

	public CertificadoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construtor da classe.
	 * 
	 * @param message
	 */
	public CertificadoException(String message) {
		this((Throwable) null);
		this.message = message;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return message;
	}

}