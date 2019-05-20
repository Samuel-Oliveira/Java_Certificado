package br.com.swconsultoria.certificado;

import java.util.Arrays;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 19/05/2019 - 13:02
 */
public enum TipoCertificadoEnum {

    REPOSITORIO_WINDOWS ("windows"),
    REPOSITORIO_MAC("mac"),
    ARQUIVO("arquivo"),
    ARQUIVO_BYTES("arquivo_bytes"),
    TOKEN_A3("a3");

    private final String descricao;

    TipoCertificadoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoCertificadoEnum valueOfDescricao(String descricao) {
        return Arrays.stream(values()).filter(x -> x.getDescricao().equals(descricao)).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
