package br.com.swconsultoria.certificado;

import java.time.LocalDate;

/**
 *
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.swconsultoria.com.br
 * 
 */

@SuppressWarnings("WeakerAccess")
public class Certificado {

    @Deprecated
	public static final String WINDOWS = "windows";
    @Deprecated
	public static final String MAC = "mac";
    @Deprecated
	public static final String ARQUIVO = "arquivo";
    @Deprecated
	public static final String ARQUIVO_BYTES = "arquivo_bytes";
    @Deprecated
	public static final String A3 = "a3";

    private static final String TSLv1_2 = "TLSv1.2";

	private String nome;
	private LocalDate vencimento;
	private Long diasRestantes;
	private String arquivo;
	private byte[] arquivoBytes;
	private String senha;
	private String cnpjCpf;
	private TipoCertificadoEnum tipoCertificado;
    private String dllA3;
    private String marcaA3;
    private String serialToken;
    private boolean valido;
    private boolean ativarProperties;
    private String sslProtocol;
    @Deprecated
    private String tipo;

    public Certificado() {
        this.setAtivarProperties(false);
        this.setSslProtocol(TSLv1_2);
    }

    public String getCnpjCpf() {
        return cnpjCpf;
    }

    public void setCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
    }

    public String getSerialToken() {
        return serialToken;
    }

    public void setSerialToken(String serialToken) {
        this.serialToken = serialToken;
    }

    public boolean isAtivarProperties() {
		return ativarProperties;
	}

	public void setAtivarProperties(boolean ativarProperties) {
		this.ativarProperties = ativarProperties;
	}

	public String getSslProtocol() {
		return sslProtocol;
	}

	public void setSslProtocol(String sslProtocol) {
		this.sslProtocol = sslProtocol;
	}

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
		return arquivoBytes;
	}

	/**
	 * @param arquivo_bytes
	 *            the arquivo_bytes to set
	 */
	public void setArquivoBytes(byte[] arquivo_bytes) {
		this.arquivoBytes = arquivo_bytes;
	}

	/**
	 * @return the tipo
	 */
	@Deprecated
	public String getTipo() {
		return tipoCertificado.getDescricao();
	}

	/**
	 * @param tipo the tipo to set
	 */
    @Deprecated
	public void setTipo(String tipo) {
		this.tipoCertificado = TipoCertificadoEnum.valueOfDescricao(tipo);
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

    public TipoCertificadoEnum getTipoCertificado() {
        return tipoCertificado;
    }

    public void setTipoCertificado(TipoCertificadoEnum tipoCertificado) {
        this.tipoCertificado = tipoCertificado;
    }
}
