package br.com.swconsultoria.certificado;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 19/05/2019 - 16:04
 */
public class TipoCertificadoEnumTest {

    @Test
    public void deveRepresentarADescricaoCorretamente() {

        assertEquals("windows", TipoCertificadoEnum.REPOSITORIO_WINDOWS.getDescricao());
        assertEquals("mac", TipoCertificadoEnum.REPOSITORIO_MAC.getDescricao());
        assertEquals("arquivo", TipoCertificadoEnum.ARQUIVO.getDescricao());
        assertEquals("arquivo_bytes", TipoCertificadoEnum.ARQUIVO_BYTES.getDescricao());
        assertEquals("a3", TipoCertificadoEnum.TOKEN_A3.getDescricao());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveLancarExcecaoCasoTenteBuscarDescricaoErrada() {
        TipoCertificadoEnum.valueOfDescricao("window");
    }

    @Test
    public void deveObterAtravesDaDescricao() {
        assertEquals(TipoCertificadoEnum.REPOSITORIO_WINDOWS, TipoCertificadoEnum.valueOfDescricao("windows"));
        assertEquals(TipoCertificadoEnum.REPOSITORIO_MAC, TipoCertificadoEnum.valueOfDescricao("mac"));
        assertEquals(TipoCertificadoEnum.ARQUIVO, TipoCertificadoEnum.valueOfDescricao("arquivo"));
        assertEquals(TipoCertificadoEnum.ARQUIVO_BYTES, TipoCertificadoEnum.valueOfDescricao("arquivo_bytes"));
        assertEquals(TipoCertificadoEnum.TOKEN_A3, TipoCertificadoEnum.valueOfDescricao("a3"));
    }

}