# Certificados por CNPJ/CPF
Exemplo de como pegar o certificado pelo CNPJ (Usando Repositorio Windows)

### Exemplo usando o Repositório do Windows: 
#### ***(Em caso de A3 será solicitado Senha)***
```java title="Cnpj.java"
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 */
public class Cnpj {

    public static void main(String[] args) {
        try {
            Certificado certificado = certificadoCnpjCpf();

            System.out.println("Alias Certificado :" + certificado.getNome());
            System.out.println("Dias Restantes Certificado :" + certificado.getDiasRestantes());
            System.out.println("Validade Certificado :" + certificado.getVencimento());

            //PARA REGISTRAR O CERTIFICADO NA SESSAO, FAÇA SOMENTE EM PROJETOS EXTERNO
            //JAVA NFE, CTE E OUTRAS APIS MINHAS JA CONTEM ESTA INICIALIZAÇÃO
            CertificadoService.inicializaCertificado(certificado, new FileInputStream(new File("caminhoCacert")));

        } catch (CertificadoException | FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static Certificado certificadoCnpjCpf() throws CertificadoException {

        String cnpj = "99999999999999";
        return CertificadoService.getCertificadoByCnpjCpf(cnpj);

    }
}
```