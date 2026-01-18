package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 19/05/2019 - 16:12
 */
class TipoCertificadoA3Test {

    @Test
    void deveRepresentarAMarcaCorretamente() {
        assertEquals("SafeWeb", TipoCertificadoA3.LEITOR_SCR3310.getMarca());
        assertEquals("eToken", TipoCertificadoA3.TOKEN_ALADDIN.getMarca());
        assertEquals("SmartCard", TipoCertificadoA3.LEITOR_GEMPC_PERTO.getMarca());
        assertEquals("Oberthur", TipoCertificadoA3.OBERTHUR.getMarca());
        assertEquals("EnterSafe", TipoCertificadoA3.ENTER_SAFE.getMarca());
    }

    @Test
    void deveRepresentarADllCorretamente() {
        assertEquals("c:/windows/system32/cmp11.dll", TipoCertificadoA3.LEITOR_SCR3310.getDll());
        assertEquals("c:/windows/system32/eTpkcs11.dll", TipoCertificadoA3.TOKEN_ALADDIN.getDll());
        assertEquals("c:/windows/system32/aetpkss1.dll", TipoCertificadoA3.LEITOR_GEMPC_PERTO.getDll());
        assertEquals("c:/windows/system32/OcsCryptoki.dll", TipoCertificadoA3.OBERTHUR.getDll());
        assertEquals("c:/windows/system32/eps2003csp11.dll", TipoCertificadoA3.ENTER_SAFE.getDll());
    }


}