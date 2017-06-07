package br.com.samuelweb.certificado;

import java.time.LocalDate;

/**
 * 
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.samuelweb.com.br
 * 
 */

public class Certificado {
	
	public static final String WINDOWS = "windows";
	public static final String ARQUIVO = "arquivo";
	public static final String ARQUIVO_BYTES = "arquivo_bytes";
	public static final String A3 = "a3";
	
	private String nome;
	
	private LocalDate vencimento;
	
	private Long diasRestantes;
	
	private String arquivo;
	
	private byte[] arquivo_bytes;

	private String senha;
	
	private String tipo;
	
	private String dllA3;
	
	private String marcaA3;
	
	private boolean valido;

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the vencimento
	 */
	public LocalDate getVencimento() {
		return vencimento;
	}

	/**
	 * @param vencimento the vencimento to set
	 */
	public void setVencimento(LocalDate vencimento) {
		this.vencimento = vencimento;
	}

	/**
	 * @return the diasRestantes
	 */
	public Long getDiasRestantes() {
		return diasRestantes;
	}

	/**
	 * @param diasRestantes the diasRestantes to set
	 */
	public void setDiasRestantes(Long diasRestantes) {
		this.diasRestantes = diasRestantes;
	}

	/**
	 * @return the valido
	 */
	public boolean isValido() {
		return valido;
	}

	/**
	 * @param valido the valido to set
	 */
	public void setValido(boolean valido) {
		this.valido = valido;
	}

	/**
	 * @return the arquivo
	 */
	public String getArquivo() {
		return arquivo;
	}

	/**
	 * @param arquivo the arquivo to set
	 */
	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	/**
	 * @return the arquivo_bytes
	 */
	public byte[] getArquivoBytes() {
		return arquivo_bytes;
	}

	/**
	 * @param arquivo_bytes
	 *            the arquivo_bytes to set
	 */
	public void setArquivoBytes(byte[] arquivo_bytes) {
		this.arquivo_bytes = arquivo_bytes;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the senha
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * @param senha the senha to set
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * @return the dllA3
	 */
	public String getDllA3() {
		return dllA3;
	}

	/**
	 * @param dllA3 the dllA3 to set
	 */
	public void setDllA3(String dllA3) {
		this.dllA3 = dllA3;
	}

	/**
	 * @return the marcaA3
	 */
	public String getMarcaA3() {
		return marcaA3;
	}

	/**
	 * @param marcaA3 the marcaA3 to set
	 */
	public void setMarcaA3(String marcaA3) {
		this.marcaA3 = marcaA3;
	}

}
