# Certificados A1

Abaixo segue exemplos de uso com certificados PFX (A1).

### Arquivo PFX
```java title="A1Pfx.java"
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 */
public class A1Pfx {

    public static void main(String[] args) {
        try{
            Certificado certificado = certifidoA1Pfx();
            System.out.println("Alias Certificado :" +certificado.getNome());
            System.out.println("Dias Restantes Certificado :" +certificado.getDiasRestantes());
            System.out.println("Validade Certificado :" +certificado.getVencimento());

            //PARA REGISTRAR O CERTIFICADO NA SESSAO, FAÇA SOMENTE EM PROJETOS EXTERNO
            //JAVA NFE, CTE E OUTRAS APIS MINHAS JA CONTEM ESTA INICIALIZAÇÃO
            CertificadoService.inicializaCertificado(certificado, new FileInputStream(new File("caminhoCacert")));

        }catch (CertificadoException | FileNotFoundException e){
            System.err.println(e.getMessage());
        }
    }

    private static Certificado certifidoA1Pfx() throws CertificadoException {
        String caminhoCertificado = "d:/teste/certificado.pfx";
        String senha = "123456";

        return CertificadoService.certificadoPfx(caminhoCertificado, senha);
    }
}
```

### Arquivo PFX Bytes
```java title="A1PfxByte.java"
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 */
public class A1PfxByte {

    public static void main(String[] args) {
        try{
            Certificado certificado = certifidoA1Pfx();
            System.out.println("Alias Certificado :" +certificado.getNome());
            System.out.println("Dias Restantes Certificado :" +certificado.getDiasRestantes());
            System.out.println("Validade Certificado :" +certificado.getVencimento());

            //PARA REGISTRAR O CERTIFICADO NA SESSAO, FAÇA SOMENTE EM PROJETOS EXTERNO
            //JAVA NFE, CTE E OUTRAS APIS MINHAS JA CONTEM ESTA INICIALIZAÇÃO
            CertificadoService.inicializaCertificado(certificado, new FileInputStream(new File("caminhoCacert")));

        }catch (CertificadoException | FileNotFoundException e){
            System.err.println(e.getMessage());
        }
    }

    private static Certificado certifidoA1Pfx() throws CertificadoException {
        byte[] certificadoByte = ... ;
        String senha = "123456";

        return CertificadoService.certificadoPfxBytes(certificadoByte, senha);
    }
}
```

