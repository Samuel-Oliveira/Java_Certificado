package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 19/05/2019 - 16:31
 */

class CertificadoServiceTest {

    private final String CERTIFICADO_CPF = "NaoUsar_CPF.pfx";
    private final String CERTIFICADO_CNPJ = "NaoUsar_CNPJ.pfx";
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
        assertEquals("certificado cpf teste", certificado.getNome());
        assertEquals(SENHA, certificado.getSenha());
        assertEquals(CPF, certificado.getCnpjCpf());
        assertEquals(LocalDate.of(2029, 5, 16), certificado.getVencimento());
        assertTrue(certificado.isValido());
        assertEquals(Long.valueOf(LocalDate.now().until(LocalDate.of(2029, 5, 16), ChronoUnit.DAYS)), certificado.getDiasRestantes());
        assertEquals("TLSv1.2", certificado.getSslProtocol());
        assertEquals(TipoCertificadoEnum.ARQUIVO, certificado.getTipoCertificado());
        assertEquals(new BigInteger("219902325555"), certificado.getNumeroSerie());
    }

    @Test
    void certificadoPfxCNPJ() throws CertificadoException, FileNotFoundException {
        Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, SENHA);
        assertEquals("certificado cnpj teste", certificado.getNome());
        assertEquals(SENHA, certificado.getSenha());
        assertEquals(CNPJ, certificado.getCnpjCpf());
        assertEquals(LocalDate.of(2029, 5, 16), certificado.getVencimento());
        assertTrue(certificado.isValido());
        assertEquals(Long.valueOf(LocalDate.now().until(LocalDate.of(2029, 5, 16), ChronoUnit.DAYS)), certificado.getDiasRestantes());
        assertEquals("TLSv1.2", certificado.getSslProtocol());
        assertEquals(TipoCertificadoEnum.ARQUIVO, certificado.getTipoCertificado());
        assertEquals(new BigInteger("219902325555"), certificado.getNumeroSerie());
    }

    @Test
    void certificadoPfxByteParametroNull() throws IOException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Certificado certificado = CertificadoService.certificadoPfxBytes(null, SENHA);
        });

        byte[] bytes = Files.readAllBytes(Paths.get(CERTIFICADO_CNPJ));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
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
        assertEquals("certificado cnpj teste", certificado.getNome());
        assertEquals(SENHA, certificado.getSenha());
        assertEquals(CNPJ, certificado.getCnpjCpf());
        assertEquals(LocalDate.of(2029, 5, 16), certificado.getVencimento());
        assertEquals(true, certificado.isValido());
        assertEquals(Long.valueOf(LocalDate.now().until(LocalDate.of(2029, 5, 16), ChronoUnit.DAYS)), certificado.getDiasRestantes());
        assertEquals("TLSv1.2", certificado.getSslProtocol());
        certificado.setSslProtocol("TLSv1.3");
        assertEquals("TLSv1.3", certificado.getSslProtocol());
        assertEquals(TipoCertificadoEnum.ARQUIVO_BYTES, certificado.getTipoCertificado());
        assertEquals(new BigInteger("219902325555"), certificado.getNumeroSerie());
    }

    @Test
    void getCertificadoByCnpjCpf() throws CertificadoException, FileNotFoundException {

        Certificado certCPF = CertificadoService.certificadoPfx(CERTIFICADO_CPF, SENHA);
        Certificado certCNPJ = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, SENHA);

        new MockUp<CertificadoService>() {
            @Mock
            public Certificado getCertificadoByCnpjCpf(String cpfCnpj) {
                return Stream.of(certCPF, certCNPJ).filter(cert -> Optional.ofNullable(cert.getCnpjCpf()).orElse("")
                        .startsWith(cpfCnpj)).findFirst().orElse(null);
            }
        };

        Certificado certificadoCPF = CertificadoService.getCertificadoByCnpjCpf(CPF);
        Certificado certificadoCNPJ = CertificadoService.getCertificadoByCnpjCpf(CNPJ);
        String CNPJ_RAIZ = "99999999";
        Certificado certificadoCNPJRaiz = CertificadoService.getCertificadoByCnpjCpf(CNPJ_RAIZ);
        Certificado certificadoInvalido = CertificadoService.getCertificadoByCnpjCpf("12312123456");

        assertEquals(certificadoCPF.getCnpjCpf(), CPF);
        assertEquals(certificadoCNPJ.getCnpjCpf(), CNPJ);
        assertEquals(certificadoCNPJRaiz.getCnpjCpf(), CPF);
        assertNull(certificadoInvalido);

    }

    @Test
    void inicaConfiguracoesCorretamente() {
        Assertions.assertDoesNotThrow(() -> {
            Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, SENHA);
            CertificadoService.inicializaCertificado(certificado);
        });
    }

    @Test
    void inicaConfiguracoesParametrosNull() throws IOException, CertificadoException {

        InputStream cacert = CertificadoServiceTest.class.getResourceAsStream("cacert");
        Certificado certificado = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, SENHA);

        //Certificado Null
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                CertificadoService.inicializaCertificado(null)
        );
        //Cacert Null
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                CertificadoService.inicializaCertificado(null)
        );
    }

    @Test
    void extraiCpfCnpjCorretamente() {
        String textoCnpj = "C=BR, O=ICP-Brasil, OU=Secretaria da Receita Federal do Brasil - RFB, CNPJ=07364617000135";
        assertEquals("07364617000135", CertificadoService.getDocumentoFromCertificado(textoCnpj));

        String textoCpf = "C=BR, O=ICP-Brasil, OU=Secretaria da Receita Federal do Brasil - RFB, CPF=99999999999";
        assertEquals("99999999999", CertificadoService.getDocumentoFromCertificado(textoCpf));

        String textoCnpjeCPF = "C=BR, O=ICP-Brasil, OU=Secretaria da Receita Federal do Brasil - RFB, CNPJ=07364617000135, CPF=99999999999";
        assertEquals("07364617000135", CertificadoService.getDocumentoFromCertificado(textoCnpjeCPF));

        String textoCpfECNPJ = "C=BR, O=ICP-Brasil, OU=Secretaria da Receita Federal do Brasil - RFB, CPF=99999999999, CNPJ=07364617000135";
        assertEquals("07364617000135", CertificadoService.getDocumentoFromCertificado(textoCpfECNPJ));

        String textoSemNenhumDocumento = "C=BR, O=ICP-Brasil, OU=Secretaria da Receita Federal do Brasil - RFB";
        assertEquals("", CertificadoService.getDocumentoFromCertificado(textoSemNenhumDocumento));
    }

}