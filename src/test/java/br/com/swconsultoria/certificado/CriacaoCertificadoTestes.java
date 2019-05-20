package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 20/05/2019 - 20:18
 */
public class CriacaoCertificadoTestes {

    @SuppressWarnings("FieldCanBeLocal")
    private final String CPF = "99999999999";
    private final String CNPJ = "99999999999999";
    private final String CERTIFICADO_CPF = "CertificadoTesteCPF.pfx";
    private final String CERTIFICADO_CNPJ = "CertificadoTesteCNPJ.pfx";
    private final String SENHA = "123456";

    @Test(expected = IllegalArgumentException.class)
    public void certificadoPfxParametroNull() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx(null, null);
    }

    @Test(expected = FileNotFoundException.class)
    public void certificadoPfxArquivoNaoExiste() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx("CertificadoNaoExiste.pfx", "");
    }

    @Test(expected = CertificadoException.class)
    public void certificadoPfxSenhaInvalida() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CPF, "");
    }

    @Test(expected = CertificadoException.class)
    public void certificadoPfxArquivoInvalido() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx("pom.xml", "");
    }

    @Test
    public void certificadoPfxCPF() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CPF, SENHA);
        assertEquals(certificado.getNome(), "certificado cpf teste");
        assertEquals(certificado.getSenha(), SENHA);
        assertEquals(certificado.getCnpjCpf(), CPF);
        assertEquals(certificado.getVencimento(), LocalDate.of(2029, 5, 16));
        assertTrue(certificado.isValido());
        assertEquals(certificado.getDiasRestantes(), Long.valueOf(LocalDate.now().until(LocalDate.of(2029, 5, 16), ChronoUnit.DAYS)));
        assertEquals(certificado.getSslProtocol(), "TLSv1.2");
        assertFalse(certificado.isAtivarProperties());
        assertEquals(certificado.getTipoCertificado(), TipoCertificadoEnum.ARQUIVO);
    }

    @Test
    public void certificadoPfxCNPJ() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, "123456");
        assertEquals(certificado.getNome(), "certificado cnpj teste");
        assertEquals(certificado.getSenha(), SENHA);
        assertEquals(certificado.getCnpjCpf(), CNPJ);
        assertEquals(certificado.getVencimento(), LocalDate.of(2029, 5, 16));
        assertTrue(certificado.isValido());
        assertEquals(certificado.getDiasRestantes(), Long.valueOf(LocalDate.now().until(LocalDate.of(2029, 5, 16), ChronoUnit.DAYS)));
        assertEquals(certificado.getSslProtocol(), "TLSv1.2");
        assertFalse(certificado.isAtivarProperties());
        assertEquals(certificado.getTipoCertificado(), TipoCertificadoEnum.ARQUIVO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void certificadoPfxByteParametroNull() throws CertificadoException {
        Certificado certificado = CertificadoService.certificadoPfxBytes(null, null);
    }

    @Test(expected = CertificadoException.class)
    public void certificadoPfxByteSenhaInvalida() throws CertificadoException, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(CERTIFICADO_CNPJ));
        Certificado certificado = CertificadoService.certificadoPfxBytes(bytes, "12345");
    }

    @Test(expected = CertificadoException.class)
    public void certificadoPfxByteInvalido() throws CertificadoException {
        Certificado certificado = CertificadoService.certificadoPfxBytes("pom.xml".getBytes(), "");
    }

    @Test
    public void certificadoPfxBytes() throws CertificadoException, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(CERTIFICADO_CNPJ));
        Certificado certificado = CertificadoService.certificadoPfxBytes(bytes, "123456");
        assertEquals(certificado.getNome(), "certificado cnpj teste");
        assertEquals(certificado.getSenha(), SENHA);
        assertEquals(certificado.getCnpjCpf(), CNPJ);
        assertEquals(certificado.getVencimento(), LocalDate.of(2029, 5, 16));
        assertEquals(certificado.isValido(), true);
        assertEquals(certificado.getDiasRestantes(), Long.valueOf(LocalDate.now().until(LocalDate.of(2029, 5, 16), ChronoUnit.DAYS)));
        assertEquals(certificado.getSslProtocol(), "TLSv1.2");
        assertEquals(certificado.isAtivarProperties(), false);
        assertEquals(certificado.getTipoCertificado(), TipoCertificadoEnum.ARQUIVO_BYTES);
    }
}
