# Certificados A3

Abaixo segue exemplos de uso com certificados A3.

### A3 Com 1 aparelho na maquina
```java title="A3Dll.java"
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.TipoCertificadoA3;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 */
public class A3Dll {

    public static void main(String[] args) {
        try{
            Certificado certificado = certificadoA3Dll();
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

    private static Certificado certificadoA3Dll() throws CertificadoException {
        String marcaA3 = TipoCertificadoA3.TOKEN_ALADDIN.getMarca();
        String dllA3 = TipoCertificadoA3.TOKEN_ALADDIN.getDll();
        String senha = "123456";

        return CertificadoService.certificadoA3(marcaA3, dllA3, senha);
    }
}
```

### A3 Com varios aparelhos na maquina
```java title="A3Alias.java"
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.TipoCertificadoA3;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 */
public class A3Alias {

    public static void main(String[] args) {
        try{
            Certificado certificado = certificadoA3Alias();
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

    private static Certificado certificadoA3Alias() throws CertificadoException {
        String marcaA3 = TipoCertificadoA3.TOKEN_ALADDIN.getMarca();
        String dllA3 = TipoCertificadoA3.TOKEN_ALADDIN.getDll();
        String aliasCertificado  = "Alias blablalba";
        String senha = "123456";

        return CertificadoService.certificadoA3(marcaA3, dllA3, senha, aliasCertificado);
    }
}

```

### A3 Com varios aparelhos na Maquina Passando o Serial
```java title="A3Serial.java"
import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.TipoCertificadoA3;
import br.com.swconsultoria.certificado.exception.CertificadoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 */
public class A3Serial {

    public static void main(String[] args) {
        try{
            Certificado certificado = certificadoA3Serial();
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

    private static Certificado certificadoA3Serial() throws CertificadoException {
        String marcaA3 = TipoCertificadoA3.TOKEN_ALADDIN.getMarca();
        String dllA3 = TipoCertificadoA3.TOKEN_ALADDIN.getDll();
        String aliasCertificado  = "Alias blablalba";
        String serial  = "999999";
        String senha = "123456";

        return CertificadoService.certificadoA3(marcaA3, dllA3, senha, aliasCertificado, serial);
    }
}

```