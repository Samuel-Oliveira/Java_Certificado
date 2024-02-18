package br.com.swconsultoria.certificado.exception;

/**
 * Exceção a ser lançada na ocorrência de falhas provenientes do Certificado.
 *
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.swconsultoria.com.br
 */

public class CertificadoException extends Exception {
	public CertificadoException(String message) {
		super(message);
	}

	public CertificadoException(String message, Throwable cause) {
		super(message, cause);
	}

	public CertificadoException(Throwable cause) {
		super(cause);
	}
}