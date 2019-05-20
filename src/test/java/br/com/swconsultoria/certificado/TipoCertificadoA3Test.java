package br.com.swconsultoria.certificado;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 19/05/2019 - 16:12
 */
public class TipoCertificadoA3Test {

    @Test
    public void deveRepresentarAMarcaCorretamente() {
        assertEquals("SafeWeb", TipoCertificadoA3.LEITOR_SCR3310.getMarca());
        assertEquals("eToken", TipoCertificadoA3.TOKEN_ALADDIN.getMarca());
        assertEquals("SmartCard", TipoCertificadoA3.LEITOR_GEMPC_PERTO.getMarca());
        assertEquals("Oberthur", TipoCertificadoA3.OBERTHUR.getMarca());
    }

    @Test
    public void deveRepresentarADllCorretamente() {
        assertEquals("c:/windows/system32/cmp11.dll", TipoCertificadoA3.LEITOR_SCR3310.getDll());
        assertEquals("c:/windows/system32/eTpkcs11.dll", TipoCertificadoA3.TOKEN_ALADDIN.getDll());
        assertEquals("c:/windows/system32/aetpkss1.dll", TipoCertificadoA3.LEITOR_GEMPC_PERTO.getDll());
        assertEquals("c:/windows/system32/OcsCryptoki.dll", TipoCertificadoA3.OBERTHUR.getDll());
    }

}