package br.com.swconsultoria.certificado;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileNotFoundException;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 19/05/2019 - 16:31
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({CertificadoService.class})
public class CertificadoServiceTest {

    @SuppressWarnings("FieldCanBeLocal")
    private final String CERTIFICADO_CPF = "CertificadoTesteCPF.pfx";
    private final String CERTIFICADO_CNPJ = "CertificadoTesteCNPJ.pfx";
    private final String CPF = "99999999999";
    private final String CNPJ = "99999999999999";
    private final String SENHA = "123456";



    @Test
    public void getCertificadoByCnpjCpf() throws CertificadoException, FileNotFoundException {

        Certificado certCPF = CertificadoService.certificadoPfx(CERTIFICADO_CPF, "123456");
        Certificado certCNPJ = CertificadoService.certificadoPfx(CERTIFICADO_CNPJ, "123456");

        PowerMockito.mockStatic(CertificadoService.class);
        PowerMockito.when(CertificadoService.getCertificadoByCnpjCpf(CPF))
                .then((Answer<Certificado>) invocation ->
                        Stream.of(certCPF, certCNPJ).filter(c -> c.getCnpjCpf().equals(CPF)).findFirst().orElse(null)
                );

        PowerMockito.when(CertificadoService.getCertificadoByCnpjCpf(CNPJ))
                .then((Answer<Certificado>) invocation ->
                        Stream.of(certCPF, certCNPJ).filter(c -> c.getCnpjCpf().equals(CNPJ)).findFirst().orElse(null)
                );

        Certificado certificadoCPF = CertificadoService.getCertificadoByCnpjCpf(CPF);
        Certificado certificadoCNPJ = CertificadoService.getCertificadoByCnpjCpf(CNPJ);

        assertEquals(certificadoCPF.getCnpjCpf(), CPF);
        assertEquals(certificadoCNPJ.getCnpjCpf(), CNPJ);

    }

}