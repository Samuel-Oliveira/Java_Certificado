# Certificados por Repositórios

### Exemplo usando o Repositório do Windows: 
#### ***(Em caso de A3 será solicitado Senha)***
```java title="RepositorioWindows.java"
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 */
public class RepositorioWindows {

    public static void main(String[] args) {
        try{
            List<Certificado> certificados = certificadosRepositorio();

            certificados.forEach( cert -> {
                System.out.println("Alias Certificado :" +cert.getNome());
                System.out.println("Dias Restantes Certificado :" +cert.getDiasRestantes());
                System.out.println("Validade Certificado :" +cert.getVencimento());
            });

            //PARA REGISTRAR O CERTIFICADO NA SESSAO, FAÇA SOMENTE EM PROJETOS EXTERNO
            //JAVA NFE, CTE E OUTRAS APIS MINHAS JA CONTEM ESTA INICIALIZAÇÃO
            Certificado certificado = certificados.get(0); // Pegou o primeiro
            CertificadoService.inicializaCertificado(certificado, new FileInputStream(new File("caminhoCacert")));

        }catch (CertificadoException | FileNotFoundException e){
            System.err.println(e.getMessage());
        }
    }

    private static List<Certificado> certificadosRepositorio() throws CertificadoException {

        return CertificadoService.listaCertificadosWindows();

    }
}
```