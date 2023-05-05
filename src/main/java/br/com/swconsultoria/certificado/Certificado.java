package br.com.swconsultoria.certificado;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.swconsultoria.com.br
 * 
 */

@SuppressWarnings("WeakerAccess")
public class Certificado {

    private static final String TSLv1_2 = "TLSv1.2";

	private String nome;
	private LocalDate vencimento;
	private LocalDateTime dataHoraVencimento;
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
    private BigInteger numeroSerie;

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
	 *Returns the nome.
 
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
	 *Returns the vencimento.
 
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
	 *Returns the dataHoraVencimento.
 
	 */
	public LocalDateTime getDataHoraVencimento() {
		return dataHoraVencimento;
	}

	/**
	 * @param dataHoraVencimento the vencimento to set
	 */
	public void setDataHoraVencimento(LocalDateTime dataHoraVencimento) {
		this.dataHoraVencimento = dataHoraVencimento;
	}

	/**
	 *Returns the diasRestantes.
 
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
	 *Returns the valido.
 
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
	 *Returns the arquivo.
 
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
	 *Returns the arquivo_bytes.
 
	 */
	public byte[] getArquivoBytes() {
		return arquivoBytes;
	}

	/**
	 * @param arquivoBytes
	 *            the arquivo_bytes to set
	 */
	public void setArquivoBytes(byte[] arquivoBytes) {
		this.arquivoBytes = arquivoBytes;
	}

	/**
	 *Returns the senha.
 
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
	 *Returns the dllA3.
 
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
	 *Returns the marcaA3.

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

    /** Returns the certificado numeroSerie */
    public BigInteger getNumeroSerie() {
        return numeroSerie;
    }

    /** The certificado numeroSerie to set.
	 * @param numeroSerie numeroSerie to set.
	 */
    public void setNumeroSerie(BigInteger numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    @Override
	public String toString() {
		return "Certificado{" +
				"nome='" + nome + '\'' +
				", dataHoraVencimento=" + dataHoraVencimento +
				", cnpjCpf='" + cnpjCpf + '\'' +
				", tipoCertificado=" + tipoCertificado +
				'}';
	}
}
