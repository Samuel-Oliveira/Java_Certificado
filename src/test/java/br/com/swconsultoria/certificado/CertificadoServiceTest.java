package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 19/05/2019 - 16:31
 */

class CertificadoServiceTest {

    private final String CERTIFICADO_CPF = "CertificadoTesteCPF.pfx";
    private final String CERTIFICADO_CNPJ = "CertificadoTesteCNPJ.pfx";
    private final String CPF = "99999999999";
    private final String CNPJ = "99999999999999";
    private final String SENHA = "123456";

    @Test
    void certificadoPfxParametroNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Certificado certificado = CertificadoService.certificadoPfx(null, SENHA);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, null);
        });
    }

    @Test
    void certificadoPfxArquivoNaoExiste() {
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            Certificado certificado = CertificadoService.certificadoPfx("CertificadoNaoExiste.pfx", "");
        });
    }

    @Test
    void certificadoPfxSenhaInvalida() {
        Assertions.assertThrows(CertificadoException.class, () -> {
            Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CPF, "");
        });
    }

    @Test
    void certificadoPfxArquivoInvalido() {
        Assertions.assertThrows(CertificadoException.class, () -> {
            Certificado certificado = CertificadoService.certificadoPfx("pom.xml", "");
        });
    }

    @Test
    void certificadoPfxCPF() throws CertificadoException, FileNotFoundException {
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
    void certificadoPfxCNPJ() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, SENHA);
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

    @Test
    void certificadoPfxByteParametroNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Certificado certificado = CertificadoService.certificadoPfxBytes(null, SENHA);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            byte[] bytes = Files.readAllBytes(Paths.get(CERTIFICADO_CNPJ));
            Certificado certificado = CertificadoService.certificadoPfxBytes(bytes, null);
        });
    }

    @Test
    void certificadoPfxByteSenhaInvalida() {
        Assertions.assertThrows(CertificadoException.class, () -> {
            byte[] bytes = Files.readAllBytes(Paths.get(CERTIFICADO_CNPJ));
            Certificado certificado = CertificadoService.certificadoPfxBytes(bytes, "12345");
        });
    }

    @Test
    void certificadoPfxByteInvalido() {
        Assertions.assertThrows(CertificadoException.class, () -> {
            Certificado certificado = CertificadoService.certificadoPfxBytes("pom.xml".getBytes(), "");
        });
    }

    @Test
    void certificadoPfxBytes() throws CertificadoException, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(CERTIFICADO_CNPJ));
        Certificado certificado = CertificadoService.certificadoPfxBytes(bytes, SENHA);
        assertEquals(certificado.getNome(), "certificado cnpj teste");
        assertEquals(certificado.getSenha(), SENHA);
        assertEquals(certificado.getCnpjCpf(), CNPJ);
        assertEquals(certificado.getVencimento(), LocalDate.of(2029, 5, 16));
        assertEquals(certificado.isValido(), true);
        assertEquals(certificado.getDiasRestantes(), Long.valueOf(LocalDate.now().until(LocalDate.of(2029, 5, 16), ChronoUnit.DAYS)));
        assertEquals(certificado.getSslProtocol(), "TLSv1.2");
        certificado.setSslProtocol("TLSv1.3");
        assertEquals(certificado.getSslProtocol(), "TLSv1.3");
        assertEquals(certificado.isAtivarProperties(), false);
        assertEquals(certificado.getTipoCertificado(), TipoCertificadoEnum.ARQUIVO_BYTES);
    }

    @Test
    void getCertificadoByCnpjCpf() throws CertificadoException, FileNotFoundException {

        Certificado certCPF = CertificadoService.certificadoPfx(CERTIFICADO_CPF, SENHA);
        Certificado certCNPJ = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, SENHA);

        new MockUp<CertificadoService>() {
            @Mock
            public Certificado getCertificadoByCnpjCpf(String cpfCnpj) {
                return  Stream.of(certCPF, certCNPJ).filter(c -> c.getCnpjCpf().equals(cpfCnpj)).findFirst().orElse(null);
            }
        };

        Certificado certificadoCPF = CertificadoService.getCertificadoByCnpjCpf(CPF);
        Certificado certificadoCNPJ = CertificadoService.getCertificadoByCnpjCpf(CNPJ);
        Certificado certificadoInvalido = CertificadoService.getCertificadoByCnpjCpf("12312123456");

        assertEquals(certificadoCPF.getCnpjCpf(), CPF);
        assertEquals(certificadoCNPJ.getCnpjCpf(), CNPJ);
        assertNull(certificadoInvalido);

    }

    @Test
    void inicaConfiguracoesCorretamente() throws IOException, CertificadoException {
        Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, SENHA);
        InputStream cacert = new FileInputStream(new File("Cacert"));
        CertificadoService.inicializaCertificado(certificado, cacert);
    }

    @Test
    void inicaConfiguracoesParametrosNull() throws IOException, CertificadoException {

        InputStream cacert = new FileInputStream(new File("Cacert"));
        Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, SENHA);

        //Certificado Null
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            CertificadoService.inicializaCertificado(null, cacert)
        );
        //Cacert Null
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            CertificadoService.inicializaCertificado(null, cacert)
        );
    }

}